package com.fjrhlm.cineverse.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.api.TmdbApiService
import com.fjrhlm.cineverse.data.api.TvEpisode

class EpisodeAdapter(private var episodes: List<TvEpisode>) :
    RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivStill: ImageView = view.findViewById(R.id.iv_episode_still)
        val tvNumber: TextView = view.findViewById(R.id.tv_episode_number)
        val tvName: TextView = view.findViewById(R.id.tv_episode_name)
        val tvOverview: TextView = view.findViewById(R.id.tv_episode_overview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        
        holder.tvNumber.text = "EPISODE ${episode.episodeNumber}"
        holder.tvName.text = episode.name
        holder.tvOverview.text = if (episode.overview.isNullOrEmpty()) {
            "No synopsis available for this episode."
        } else {
            episode.overview
        }

        val stillUrl = episode.stillPath?.let { TmdbApiService.IMAGE_BASE_URL + it }
        Glide.with(holder.itemView.context)
            .load(stillUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .centerCrop()
            .into(holder.ivStill)
    }

    override fun getItemCount(): Int = episodes.size

    fun updateEpisodes(newEpisodes: List<TvEpisode>) {
        episodes = newEpisodes
        notifyDataSetChanged()
    }
}
