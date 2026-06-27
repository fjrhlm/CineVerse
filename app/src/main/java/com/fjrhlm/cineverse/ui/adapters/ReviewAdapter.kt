package com.fjrhlm.cineverse.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.api.Review
import com.fjrhlm.cineverse.data.api.TmdbApiService

class ReviewAdapter(private val reviews: List<Review>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.tvUsername.text = review.author
        holder.tvContent.text = review.content
        holder.tvDate.text = review.createdAt?.substringBefore("T") ?: "N/A"
        
        val rating = review.authorDetails?.rating
        holder.tvRating.text = if (rating != null) String.format("%.1f", rating) else "10.0" // Default or mock rating

        // Parse avatar URL
        var avatarUrl: String? = null
        val path = review.authorDetails?.avatarPath
        if (path != null) {
            avatarUrl = when {
                path.startsWith("/http") -> path.substring(1)
                path.startsWith("http") -> path
                else -> TmdbApiService.IMAGE_BASE_URL + path
            }
        }

        Glide.with(holder.itemView.context)
            .load(avatarUrl)
            .placeholder(android.R.drawable.sym_def_app_icon)
            .error(android.R.drawable.sym_def_app_icon)
            .into(holder.ivAvatar)
    }

    override fun getItemCount(): Int = reviews.size

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.findViewById(R.id.iv_user_avatar)
        val tvUsername: TextView = view.findViewById(R.id.tv_username)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val tvRating: TextView = view.findViewById(R.id.tv_user_rating)
        val tvContent: TextView = view.findViewById(R.id.tv_content)
    }
}
