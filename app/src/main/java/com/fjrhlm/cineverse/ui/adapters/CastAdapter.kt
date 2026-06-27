package com.fjrhlm.cineverse.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.api.Cast
import com.fjrhlm.cineverse.data.api.TmdbApiService

class CastAdapter(private val castList: List<Cast>) : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cast, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val cast = castList[position]
        holder.tvName.text = cast.name
        holder.tvCharacter.text = cast.character ?: "N/A"

        val imageUrl = cast.profilePath?.let { TmdbApiService.IMAGE_BASE_URL + it }
        
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(android.R.drawable.sym_def_app_icon)
            .error(android.R.drawable.sym_def_app_icon)
            .into(holder.ivAvatar)
    }

    override fun getItemCount(): Int = castList.size

    class CastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.findViewById(R.id.iv_avatar)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvCharacter: TextView = view.findViewById(R.id.tv_character)
    }
}
