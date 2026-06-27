package com.fjrhlm.cineverse.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.api.GenreResponse
import com.fjrhlm.cineverse.data.api.RetrofitClient
import com.fjrhlm.cineverse.ui.adapters.GenreAdapter
import com.fjrhlm.cineverse.data.local.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryFragment : Fragment(R.layout.fragment_category) {

    private lateinit var rvGenres: RecyclerView
    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvGenres = view.findViewById(R.id.rv_genres)
        btnBack = view.findViewById(R.id.btn_back)
        progressBar = view.findViewById(R.id.progress_bar)
        sessionManager = SessionManager(requireContext())

        rvGenres.layoutManager = GridLayoutManager(requireContext(), 2)

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        fetchGenres()
    }

    private fun fetchGenres() {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getGenres(language = getApiLanguage()).enqueue(object : Callback<GenreResponse> {
            override fun onResponse(call: Call<GenreResponse>, response: Response<GenreResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val genres = response.body()?.genres ?: emptyList()
                    rvGenres.adapter = GenreAdapter(genres) { genre ->
                        val bundle = Bundle().apply {
                            putString("genreId", genre.id.toString())
                            putString("genreName", genre.name)
                        }
                        findNavController().navigate(R.id.action_categoryFragment_to_exploreFragment, bundle)
                    }
                }
            }

            override fun onFailure(call: Call<GenreResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getApiLanguage(): String {
        return if (sessionManager.getLanguage() == "id") "id-ID" else "en-US"
    }
}
