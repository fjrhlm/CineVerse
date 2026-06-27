package com.fjrhlm.cineverse.ui.detail

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
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.api.RetrofitClient
import com.fjrhlm.cineverse.data.api.ReviewsResponse
import com.fjrhlm.cineverse.ui.adapters.ReviewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewFragment : Fragment(R.layout.fragment_review) {

    private lateinit var tvMovieTitle: TextView
    private lateinit var rvReviews: RecyclerView
    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoReviews: TextView

    private var movieId: Int = -1
    private var movieTitle: String = ""
    private var mediaType: String = "movie"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieId = arguments?.getInt("movieId", -1) ?: -1
        movieTitle = arguments?.getString("movieTitle", "Movie") ?: "Movie"
        mediaType = arguments?.getString("mediaType", "movie") ?: "movie"

        tvMovieTitle = view.findViewById(R.id.tv_movie_title)
        rvReviews = view.findViewById(R.id.rv_reviews)
        btnBack = view.findViewById(R.id.btn_back)
        progressBar = view.findViewById(R.id.progress_bar)
        tvNoReviews = view.findViewById(R.id.tv_no_reviews)

        tvMovieTitle.text = movieTitle
        rvReviews.layoutManager = LinearLayoutManager(requireContext())

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (movieId != -1) {
            fetchReviews()
        }
    }

    private fun fetchReviews() {
        progressBar.visibility = View.VISIBLE
        tvNoReviews.visibility = View.GONE

        val apiService = RetrofitClient.instance
        val call: Call<ReviewsResponse> = if (mediaType == "tv") {
            apiService.getTvReviews(movieId)
        } else {
            apiService.getMovieReviews(movieId)
        }

        call.enqueue(object : Callback<ReviewsResponse> {
            override fun onResponse(call: Call<ReviewsResponse>, response: Response<ReviewsResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val reviews = response.body()?.results ?: emptyList()
                    if (reviews.isEmpty()) {
                        tvNoReviews.visibility = View.VISIBLE
                    } else {
                        rvReviews.adapter = ReviewAdapter(reviews)
                    }
                }
            }

            override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load reviews", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
