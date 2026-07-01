package com.fjrhlm.cineverse.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.api.CreditsResponse
import com.fjrhlm.cineverse.data.api.Movie
import com.fjrhlm.cineverse.data.api.RetrofitClient
import com.fjrhlm.cineverse.data.api.TmdbApiService
import com.fjrhlm.cineverse.data.api.TvSeasonDetails
import com.fjrhlm.cineverse.data.api.VideoResponse
import com.fjrhlm.cineverse.data.local.DbHelper
import com.fjrhlm.cineverse.data.local.SessionManager
import com.fjrhlm.cineverse.ui.adapters.CastAdapter
import com.fjrhlm.cineverse.ui.adapters.EpisodeAdapter
import com.google.android.material.button.MaterialButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private lateinit var ivBackdrop: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvReleaseDate: TextView
    private lateinit var btnTrailer: MaterialButton
    private lateinit var tvOverview: TextView
    private lateinit var rvCast: RecyclerView
    
    private lateinit var btnBack: ImageButton
    private lateinit var btnWatchlistToggle: ImageButton
    private lateinit var btnReviews: MaterialButton
    private lateinit var progressBar: ProgressBar

    // UI Tambahan khusus TV Show / Anime
    private lateinit var layoutSeasonsEpisodes: LinearLayout
    private lateinit var spinnerSeasons: Spinner
    private lateinit var rvEpisodes: RecyclerView
    private lateinit var episodeAdapter: EpisodeAdapter

    private lateinit var dbHelper: DbHelper
    private lateinit var sessionManager: SessionManager
    private var movieId: Int = -1
    private var mediaType: String = "movie"
    private var movieTitle: String = ""
    private var activeMovie: Movie? = null
    private var isWatchlisted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DbHelper(requireContext())
        sessionManager = SessionManager(requireContext())
        movieId = arguments?.getInt("movieId", -1) ?: -1
        mediaType = arguments?.getString("mediaType", "movie") ?: "movie"

        ivBackdrop = view.findViewById(R.id.iv_backdrop)
        tvTitle = view.findViewById(R.id.tv_title)
        tvRating = view.findViewById(R.id.tv_rating)
        tvReleaseDate = view.findViewById(R.id.tv_release_date)
        btnTrailer = view.findViewById(R.id.btn_trailer)
        tvOverview = view.findViewById(R.id.tv_overview)
        rvCast = view.findViewById(R.id.rv_cast)
        
        btnBack = view.findViewById(R.id.btn_back)
        btnWatchlistToggle = view.findViewById(R.id.btn_watchlist_toggle)
        btnReviews = view.findViewById(R.id.btn_reviews)
        progressBar = view.findViewById(R.id.progress_bar)

        // Inisialisasi UI Seasons &amp; Episodes
        layoutSeasonsEpisodes = view.findViewById(R.id.layout_seasons_episodes)
        spinnerSeasons = view.findViewById(R.id.spinner_seasons)
        rvEpisodes = view.findViewById(R.id.rv_episodes)

        rvCast.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvEpisodes.layoutManager = LinearLayoutManager(requireContext())
        
        episodeAdapter = EpisodeAdapter(emptyList())
        rvEpisodes.adapter = episodeAdapter

        if (movieId != -1) {
            fetchDetails()
            fetchCredits()
            checkWatchlistStatus()
        } else {
            Toast.makeText(requireContext(), "Invalid item ID", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        btnWatchlistToggle.setOnClickListener {
            toggleWatchlist()
        }

        btnTrailer.setOnClickListener {
            watchTrailer()
        }

        btnReviews.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("movieId", movieId)
                putString("movieTitle", movieTitle)
                putString("mediaType", mediaType)
            }
            findNavController().navigate(R.id.action_detailFragment_to_reviewFragment, bundle)
        }
    }

    private fun fetchDetails() {
        progressBar.visibility = View.VISIBLE
        val apiService = RetrofitClient.instance
        val lang = getApiLanguage()

        val call: Call<Movie> = if (mediaType == "tv") {
            apiService.getTvDetails(movieId, language = lang)
        } else {
            apiService.getMovieDetails(movieId, language = lang)
        }

        call.enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                if (response.isSuccessful) {
                    val item = response.body()
                    if (item != null) {
                        if (item.overview.isNullOrBlank() && lang == "id-ID") {
                            fetchDetailsFallbackEnglish()
                            return
                        }
                        progressBar.visibility = View.GONE
                        activeMovie = item
                        movieTitle = item.displayTitle
                        bindDetails(item)
                    } else {
                        progressBar.visibility = View.GONE
                    }
                } else {
                    progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchDetailsFallbackEnglish() {
        val apiService = RetrofitClient.instance
        val call: Call<Movie> = if (mediaType == "tv") {
            apiService.getTvDetails(movieId, language = "en-US")
        } else {
            apiService.getMovieDetails(movieId, language = "en-US")
        }

        call.enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val item = response.body()
                    if (item != null) {
                        activeMovie = item
                        movieTitle = item.displayTitle
                        bindDetails(item)
                    }
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun bindDetails(movie: Movie) {
        tvTitle.text = movie.displayTitle
        tvRating.text = String.format("%.1f / 10", movie.voteAverage)
        tvOverview.text = movie.overview ?: "No overview available."

        val isIndo = sessionManager.getLanguage() == "id"

        if (mediaType == "tv") {
            val seasonsCount = movie.numberOfSeasons ?: 1
            val episodesCount = movie.numberOfEpisodes ?: 0
            val seasonText = if (isIndo) "Season" else "Seasons"
            val episodeText = if (isIndo) "Episode" else "Episodes"
            
            tvReleaseDate.text = "$seasonsCount $seasonText • $episodesCount $episodeText"
            
            setupSeasonsSpinner(seasonsCount)
            layoutSeasonsEpisodes.visibility = View.VISIBLE
        } else {
            tvReleaseDate.text = if (isIndo) "Rilis: ${movie.displayDate}" else "Released: ${movie.displayDate}"
            layoutSeasonsEpisodes.visibility = View.GONE
        }

        val backdropUrl = movie.backdropPath?.let { TmdbApiService.BACKDROP_BASE_URL + it }
        
        Glide.with(requireContext())
            .load(backdropUrl)
            .placeholder(android.R.color.darker_gray)
            .into(ivBackdrop)
    }

    private fun setupSeasonsSpinner(seasonsCount: Int) {
        val seasonsList = (1..seasonsCount).map { "Season $it" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, seasonsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSeasons.adapter = adapter

        spinnerSeasons.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSeason = position + 1
                fetchSeasonEpisodes(selectedSeason)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchSeasonEpisodes(seasonNumber: Int) {
        val apiService = RetrofitClient.instance
        val lang = getApiLanguage()

        apiService.getTvSeasonDetails(movieId, seasonNumber, language = lang)
            .enqueue(object : Callback<TvSeasonDetails> {
                override fun onResponse(call: Call<TvSeasonDetails>, response: Response<TvSeasonDetails>) {
                    if (response.isSuccessful) {
                        val episodes = response.body()?.episodes ?: emptyList()
                        episodeAdapter.updateEpisodes(episodes)
                    }
                }

                override fun onFailure(call: Call<TvSeasonDetails>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failed to load episodes", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun watchTrailer() {
        progressBar.visibility = View.VISIBLE
        val apiService = RetrofitClient.instance
        val call = if (mediaType == "tv") {
            apiService.getTvVideos(movieId)
        } else {
            apiService.getMovieVideos(movieId)
        }

        call.enqueue(object : Callback<VideoResponse> {
            override fun onResponse(call: Call<VideoResponse>, response: Response<VideoResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val videos = response.body()?.results ?: emptyList()
                    val trailer = videos.firstOrNull {
                        it.site.lowercase() == "youtube" &&
                                (it.type.lowercase() == "trailer" || it.type.lowercase() == "teaser")
                    } ?: videos.firstOrNull { it.site.lowercase() == "youtube" }

                    if (trailer != null) {
                        showTrailerDialog(trailer.key)
                    } else {
                        Toast.makeText(requireContext(), "Trailer not available for this item", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Trailer not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Network error, please try again", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showTrailerDialog(videoKey: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_trailer, null)
        val youtubePlayerView = dialogView.findViewById<YouTubePlayerView>(R.id.youtube_player_view)
        
        lifecycle.addObserver(youtubePlayerView)

        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_CineVerse)
            .setView(dialogView)
            .create()

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoKey, 0f)
            }
        })

        dialog.setOnDismissListener {
            youtubePlayerView.release()
        }

        dialog.show()
    }

    private fun fetchCredits() {
        val apiService = RetrofitClient.instance
        val call: Call<CreditsResponse> = if (mediaType == "tv") {
            apiService.getTvCredits(movieId)
        } else {
            apiService.getMovieCredits(movieId)
        }

        call.enqueue(object : Callback<CreditsResponse> {
            override fun onResponse(call: Call<CreditsResponse>, response: Response<CreditsResponse>) {
                if (response.isSuccessful) {
                    val cast = response.body()?.cast ?: emptyList()
                    rvCast.adapter = CastAdapter(cast)
                }
            }

            override fun onFailure(call: Call<CreditsResponse>, t: Throwable) {}
        })
    }

    private fun checkWatchlistStatus() {
        isWatchlisted = dbHelper.isInWatchlist(movieId)
        updateWatchlistIcon()
    }

    private fun updateWatchlistIcon() {
        if (isWatchlisted) {
            btnWatchlistToggle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.aksen_amber))
        } else {
            btnWatchlistToggle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    private fun toggleWatchlist() {
        val item = activeMovie ?: return
        if (isWatchlisted) {
            val removed = dbHelper.removeFromWatchlist(movieId)
            if (removed) {
                isWatchlisted = false
                Toast.makeText(requireContext(), "Removed from Watchlist", Toast.LENGTH_SHORT).show()
            }
        } else {
            val added = dbHelper.addToWatchlist(
                movieId = item.id,
                title = item.displayTitle,
                posterPath = item.posterPath,
                rating = item.voteAverage,
                releaseDate = item.displayDate,
                mediaType = mediaType
            )
            if (added) {
                isWatchlisted = true
                Toast.makeText(requireContext(), "Added to Watchlist", Toast.LENGTH_SHORT).show()
            }
        }
        updateWatchlistIcon()
    }

    private fun getApiLanguage(): String {
        return if (sessionManager.getLanguage() == "id") "id-ID" else "en-US"
    }
}
