package com.fjrhlm.cineverse.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.api.Movie
import com.fjrhlm.cineverse.data.api.MovieResponse
import com.fjrhlm.cineverse.data.api.RetrofitClient
import com.fjrhlm.cineverse.data.local.SessionManager
import com.fjrhlm.cineverse.ui.adapters.MovieAdapter
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var tvGreeting: TextView
    private lateinit var btnGenres: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tabLayout: TabLayout

    private lateinit var rvNowPlaying: RecyclerView
    private lateinit var rvPopular: RecyclerView
    private lateinit var rvTopRated: RecyclerView

    private lateinit var cardFeatured: View
    private lateinit var ivFeaturedBackdrop: android.widget.ImageView
    private lateinit var tvFeaturedTitle: TextView
    private lateinit var btnFeaturedPlay: View

    private lateinit var sessionManager: SessionManager
    private var currentMediaType = "movie"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        tvGreeting = view.findViewById(R.id.tv_greeting)
        btnGenres = view.findViewById(R.id.btn_genres)
        progressBar = view.findViewById(R.id.progress_bar)
        tabLayout = view.findViewById(R.id.tab_layout)

        rvNowPlaying = view.findViewById(R.id.rv_now_playing)
        rvPopular = view.findViewById(R.id.rv_popular)
        rvTopRated = view.findViewById(R.id.rv_top_rated)

        cardFeatured = view.findViewById(R.id.card_featured)
        ivFeaturedBackdrop = view.findViewById(R.id.iv_featured_backdrop)
        tvFeaturedTitle = view.findViewById(R.id.tv_featured_title)
        btnFeaturedPlay = view.findViewById(R.id.btn_featured_play)

        tvGreeting.text = "Hello, ${sessionManager.getUsername()}!"

        setupRecyclerViews()
        
        // Default load movies
        fetchMovieData()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        currentMediaType = "movie"
                        fetchMovieData()
                    }
                    1 -> {
                        currentMediaType = "tv"
                        fetchTvData()
                    }
                    2 -> {
                        currentMediaType = "tv" // Anime are listed as TV in TMDB
                        fetchAnimeData()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        btnGenres.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_categoryFragment)
        }
    }

    private fun setupRecyclerViews() {
        rvNowPlaying.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvPopular.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvTopRated.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fetchMovieData() {
        progressBar.visibility = View.VISIBLE

        val apiService = RetrofitClient.instance
        val lang = getApiLanguage()

        // 1. Fetch Now Playing
        apiService.getNowPlaying(language = lang).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    if (movies.isNotEmpty()) {
                        bindFeaturedMovie(movies.first(), "movie")
                    }
                    rvNowPlaying.adapter = MovieAdapter(movies, onItemClick = { movie ->
                        navigateToDetail(movie.id, "movie")
                    })
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load now playing movies", Toast.LENGTH_SHORT).show()
            }
        })

        // 2. Fetch Popular
        apiService.getPopular(language = lang).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    rvPopular.adapter = MovieAdapter(movies, onItemClick = { movie ->
                        navigateToDetail(movie.id, "movie")
                    })
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {}
        })

        // 3. Fetch Top Rated
        apiService.getTopRated(language = lang).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    rvTopRated.adapter = MovieAdapter(movies, onItemClick = { movie ->
                        navigateToDetail(movie.id, "movie")
                    })
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {}
        })
    }

    private fun fetchTvData() {
        progressBar.visibility = View.VISIBLE

        val apiService = RetrofitClient.instance
        val lang = getApiLanguage()

        // 1. Fetch Popular TV Shows (as slider)
        apiService.getPopularTv(language = lang).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val tvShows = response.body()?.results ?: emptyList()
                    if (tvShows.isNotEmpty()) {
                        bindFeaturedMovie(tvShows.first(), "tv")
                    }
                    rvNowPlaying.adapter = MovieAdapter(tvShows, onItemClick = { tv ->
                        navigateToDetail(tv.id, "tv")
                    })
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load TV shows", Toast.LENGTH_SHORT).show()
            }
        })

        // 2. Fetch Popular TV
        apiService.getPopularTv(language = lang).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val tvShows = response.body()?.results ?: emptyList()
                    rvPopular.adapter = MovieAdapter(tvShows, onItemClick = { tv ->
                        navigateToDetail(tv.id, "tv")
                    })
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {}
        })

        // 3. Fetch Top Rated TV
        apiService.getTopRatedTv(language = lang).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val tvShows = response.body()?.results ?: emptyList()
                    rvTopRated.adapter = MovieAdapter(tvShows, onItemClick = { tv ->
                        navigateToDetail(tv.id, "tv")
                    })
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {}
        })
    }

    private fun fetchAnimeData() {
        progressBar.visibility = View.VISIBLE

        val apiService = RetrofitClient.instance
        val lang = getApiLanguage()

        // 1. Fetch Anime (discover TV Animation with Japanese language) page 1
        apiService.discoverTv(genreId = "16", originalLanguage = "ja", language = lang, page = 1).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val anime = response.body()?.results ?: emptyList()
                    if (anime.isNotEmpty()) {
                        bindFeaturedMovie(anime.first(), "tv")
                    }
                    rvNowPlaying.adapter = MovieAdapter(anime, onItemClick = { item ->
                        navigateToDetail(item.id, "tv")
                    })
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load Anime", Toast.LENGTH_SHORT).show()
            }
        })

        // 2. Fetch Anime page 2
        apiService.discoverTv(genreId = "16", originalLanguage = "ja", language = lang, page = 2).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val anime = response.body()?.results ?: emptyList()
                    rvPopular.adapter = MovieAdapter(anime, onItemClick = { item ->
                        navigateToDetail(item.id, "tv")
                    })
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {}
        })

        // 3. Fetch Anime page 3
        apiService.discoverTv(genreId = "16", originalLanguage = "ja", language = lang, page = 3).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val anime = response.body()?.results ?: emptyList()
                    rvTopRated.adapter = MovieAdapter(anime, onItemClick = { item ->
                        navigateToDetail(item.id, "tv")
                    })
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {}
        })
    }

    private fun bindFeaturedMovie(movie: Movie, mediaType: String) {
        cardFeatured.visibility = View.VISIBLE
        tvFeaturedTitle.text = movie.displayTitle
        val imageUrl = movie.backdropPath?.let { com.fjrhlm.cineverse.data.api.TmdbApiService.IMAGE_BASE_URL + it }
            ?: movie.posterPath?.let { com.fjrhlm.cineverse.data.api.TmdbApiService.IMAGE_BASE_URL + it }

        Glide.with(this)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(ivFeaturedBackdrop)

        btnFeaturedPlay.setOnClickListener {
            navigateToDetail(movie.id, mediaType)
        }
    }

    private fun getApiLanguage(): String {
        return if (sessionManager.getLanguage() == "id") "id-ID" else "en-US"
    }

    private fun navigateToDetail(movieId: Int, mediaType: String) {
        val bundle = Bundle().apply {
            putInt("movieId", movieId)
            putString("mediaType", mediaType)
        }
        findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
    }
}
