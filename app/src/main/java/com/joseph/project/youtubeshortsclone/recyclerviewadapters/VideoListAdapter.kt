package com.joseph.project.youtubeshortsclone.recyclerviewadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joseph.project.youtubeshortsclone.R
import com.joseph.project.youtubeshortsclone.model.Post

class VideoListAdapter(private val vList: ArrayList<Post>) :
    RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>() {
    private var onClickListener: OnClickListener? = null
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val videoCards =
            LayoutInflater.from(parent.context).inflate(R.layout.video_card, parent, false)
        context = parent.context
        return VideoViewHolder(videoCards)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoElement = vList[position]
        holder.author?.text = videoElement.creator.name
        "@${videoElement.creator.handle}".also {
            holder.handle?.text = it
        }

        Glide.with(context)
            .load(videoElement.creator.pic)
            .fitCenter()
            .placeholder(R.drawable.photo_thumbnail)
            .into(holder.thumbnail)

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, videoElement)
        }

    }

    interface OnClickListener {
        fun onClick(position: Int, model: Post)
    }

    fun setClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int = vList.size

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val author: TextView? = itemView.findViewById(R.id.author)
        val handle: TextView? = itemView.findViewById(R.id.handle)
        val thumbnail: ImageView = itemView.findViewById(R.id.imageView)
    }
}