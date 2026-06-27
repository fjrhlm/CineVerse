package com.fjrhlm.cineverse.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.api.Movie
import com.fjrhlm.cineverse.data.api.TmdbApiService

class MovieAdapter(
    private var movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit,
    private val isWatchlistMode: Boolean = false,
    private val onDeleteClick: ((Movie) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun updateData(newMovies: List<Movie>) {
        this.movies = newMovies
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isWatchlistMode) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            val view = inflater.inflate(R.layout.item_movie_watchlist, parent, false)
            WatchlistViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_movie, parent, false)
            GridMovieViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = movies[position]
        val imageUrl = movie.posterPath?.let { TmdbApiService.IMAGE_BASE_URL + it }

        if (holder is WatchlistViewHolder) {
            holder.tvTitle.text = movie.displayTitle
            holder.tvReleaseDate.text = "Released: ${movie.displayDate}"
            holder.tvRating.text = String.format("%.1f", movie.voteAverage)

            // Seeded random progress based on ID
            val progressVal = kotlin.random.Random(movie.id.toLong()).nextInt(30, 96)
            holder.pbProgress.progress = progressVal

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.ivPoster)

            holder.itemView.setOnClickListener { onItemClick(movie) }
            holder.btnDelete.setOnClickListener { onDeleteClick?.invoke(movie) }

        } else if (holder is GridMovieViewHolder) {
            holder.tvTitle.text = movie.displayTitle
            holder.tvRating.text = String.format("%.1f", movie.voteAverage)

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.ivPoster)

            holder.itemView.setOnClickListener { onItemClick(movie) }
        }
    }

    override fun getItemCount(): Int = movies.size

    class GridMovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster: ImageView = view.findViewById(R.id.iv_poster)
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvRating: TextView = view.findViewById(R.id.tv_rating)
    }

    class WatchlistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster: ImageView = view.findViewById(R.id.iv_poster)
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvReleaseDate: TextView = view.findViewById(R.id.tv_release_date)
        val tvRating: TextView = view.findViewById(R.id.tv_rating)
        val pbProgress: android.widget.ProgressBar = view.findViewById(R.id.pb_watchlist_progress)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }
}
