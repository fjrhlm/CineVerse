package com.fjrhlm.cineverse.ui.main

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.api.Movie
import com.fjrhlm.cineverse.data.api.MovieResponse
import com.fjrhlm.cineverse.data.api.RetrofitClient
import com.fjrhlm.cineverse.ui.adapters.MovieAdapter
import com.fjrhlm.cineverse.data.local.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreFragment : Fragment(R.layout.fragment_explore) {

    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageButton
    private lateinit var tvResultsTitle: TextView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    private var movieAdapter: MovieAdapter? = null
    private var genreId: String = "none"
    private var genreName: String = "none"
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        etSearch = view.findViewById(R.id.et_search)
        btnSearch = view.findViewById(R.id.btn_search)
        tvResultsTitle = view.findViewById(R.id.tv_results_title)
        rvSearchResults = view.findViewById(R.id.rv_search_results)
        progressBar = view.findViewById(R.id.progress_bar)
        tvEmpty = view.findViewById(R.id.tv_empty)

        // Read navigation arguments
        arguments?.let {
            genreId = it.getString("genreId", "none")
            genreName = it.getString("genreName", "none")
        }

        setupRecyclerView()

        if (genreId != "none") {
            // Fetch by category/genre (discover movies by default)
            etSearch.setText("")
            tvResultsTitle.text = "Genre: $genreName"
            fetchMoviesByGenre(genreId)
        } else {
            // Load default popular/trending
            tvResultsTitle.text = "Trending Movies"
            loadTrendingMovies()
        }

        btnSearch.setOnClickListener {
            performSearch()
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun setupRecyclerView() {
        rvSearchResults.layoutManager = GridLayoutManager(requireContext(), 2)
        movieAdapter = MovieAdapter(emptyList(), onItemClick = { movie ->
            // Open as tv or movie depending on metadata
            val type = movie.mediaType ?: "movie"
            navigateToDetail(movie.id, type)
        })
        rvSearchResults.adapter = movieAdapter
    }

    private fun performSearch() {
        val query = etSearch.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(requireContext(), "Enter search query", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        tvResultsTitle.text = "Results for: \"$query\""

        // Call multi-search to fetch both movies and TV series
        RetrofitClient.instance.searchMulti(query, language = getApiLanguage()).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val results = response.body()?.results ?: emptyList()
                    
                    // Filter out results that are people (TMDB search multi can return actors/directors as media_type "person")
                    val moviesAndTv = results.filter { it.mediaType == "movie" || it.mediaType == "tv" }
                    
                    movieAdapter?.updateData(moviesAndTv)
                    if (moviesAndTv.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchMoviesByGenre(id: String) {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        RetrofitClient.instance.discoverMovies(genreId = id, language = getApiLanguage()).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    // Set explicitly to movie type
                    val mapped = movies.map { it.copy(mediaType = "movie") }
                    movieAdapter?.updateData(mapped)
                    if (mapped.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load category movies", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadTrendingMovies() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        RetrofitClient.instance.getPopular(language = getApiLanguage()).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    val mapped = movies.map { it.copy(mediaType = "movie") }
                    movieAdapter?.updateData(mapped)
                    if (mapped.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load trending movies", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getApiLanguage(): String {
        return if (sessionManager.getLanguage() == "id") "id-ID" else "en-US"
    }

    private fun navigateToDetail(movieId: Int, mediaType: String) {
        val bundle = Bundle().apply {
            putInt("movieId", movieId)
            putString("mediaType", mediaType)
        }
        findNavController().navigate(R.id.action_exploreFragment_to_detailFragment, bundle)
    }
}
