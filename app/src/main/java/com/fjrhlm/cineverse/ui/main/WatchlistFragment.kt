package com.fjrhlm.cineverse.ui.main

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.api.Movie
import com.fjrhlm.cineverse.data.local.DbHelper
import com.fjrhlm.cineverse.ui.adapters.MovieAdapter
import com.google.android.material.button.MaterialButton

class WatchlistFragment : Fragment(R.layout.fragment_watchlist) {

    private lateinit var rvWatchlist: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var btnDiscover: MaterialButton
    private lateinit var dbHelper: DbHelper
    private lateinit var tabLayout: com.google.android.material.tabs.TabLayout

    private var movieAdapter: MovieAdapter? = null
    private var currentFilter = 0 // 0 = Semua, 1 = Film, 2 = TV Series

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DbHelper(requireContext())

        rvWatchlist = view.findViewById(R.id.rv_watchlist)
        layoutEmpty = view.findViewById(R.id.layout_empty)
        btnDiscover = view.findViewById(R.id.btn_discover)
        tabLayout = view.findViewById(R.id.watchlist_tab_layout)

        rvWatchlist.layoutManager = LinearLayoutManager(requireContext())

        tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                currentFilter = tab?.position ?: 0
                loadWatchlist()
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })

        loadWatchlist()

        btnDiscover.setOnClickListener {
            findNavController().navigate(R.id.exploreFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        loadWatchlist()
    }

    private fun loadWatchlist() {
        var localMovies = dbHelper.getWatchlistMovies()
        
        // Filter based on selected tab
        if (currentFilter == 1) {
            localMovies = localMovies.filter { it.mediaType == "movie" }
        } else if (currentFilter == 2) {
            localMovies = localMovies.filter { it.mediaType == "tv" }
        }
        
        if (localMovies.isEmpty()) {
            rvWatchlist.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
        } else {
            rvWatchlist.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE

            // Map LocalMovie items to TMDB Movie items for Adapter compatibility
            val mappedMovies = localMovies.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    name = null,
                    overview = null,
                    posterPath = it.posterPath,
                    backdropPath = null,
                    voteAverage = it.voteAverage,
                    releaseDate = it.releaseDate,
                    firstAirDate = null,
                    mediaType = it.mediaType
                )
            }

            movieAdapter = MovieAdapter(
                movies = mappedMovies,
                isWatchlistMode = true,
                onItemClick = { movie ->
                    navigateToDetail(movie.id, movie.mediaType ?: "movie")
                },
                onDeleteClick = { movie ->
                    removeFromWatchlist(movie.id)
                }
            )
            rvWatchlist.adapter = movieAdapter
        }
    }

    private fun removeFromWatchlist(movieId: Int) {
        val deleted = dbHelper.removeFromWatchlist(movieId)
        if (deleted) {
            Toast.makeText(requireContext(), "Removed from Watchlist", Toast.LENGTH_SHORT).show()
            loadWatchlist()
        }
    }

    private fun navigateToDetail(movieId: Int, mediaType: String) {
        val bundle = Bundle().apply {
            putInt("movieId", movieId)
            putString("mediaType", mediaType)
        }
        findNavController().navigate(R.id.action_watchlistFragment_to_detailFragment, bundle)
    }
}
