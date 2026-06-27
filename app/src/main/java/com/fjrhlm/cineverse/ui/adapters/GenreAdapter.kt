package com.fjrhlm.cineverse.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.api.Genre

class GenreAdapter(
    private val genres: List<Genre>,
    private val onGenreClick: (Genre) -> Unit
) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_genre, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genres[position]
        holder.tvGenreName.text = genre.name
        
        // Dynamic seeded random count
        val count = kotlin.random.Random(genre.id.toLong()).nextInt(120, 851)
        holder.tvGenreCount.text = "$count judul"
        
        holder.itemView.setOnClickListener { onGenreClick(genre) }
    }

    override fun getItemCount(): Int = genres.size

    class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvGenreName: TextView = view.findViewById(R.id.tv_genre_name)
        val tvGenreCount: TextView = view.findViewById(R.id.tv_genre_count)
    }
}
