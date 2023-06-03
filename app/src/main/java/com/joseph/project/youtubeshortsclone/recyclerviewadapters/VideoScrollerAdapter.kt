package com.joseph.project.youtubeshortsclone.recyclerviewadapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.joseph.project.youtubeshortsclone.R
import com.joseph.project.youtubeshortsclone.model.Post
import java.io.File

@SuppressLint("UnsafeOptInUsageError")
class VideoScrollerAdapter(private val vList: ArrayList<Post>, private val context: Context) :
    RecyclerView.Adapter<VideoScrollerAdapter.VideoScrollViewHolder>() {
    private lateinit var player: ExoPlayer
    private var simpleCache: SimpleCache

    init {
        val evict = LeastRecentlyUsedCacheEvictor((100 * 1024 * 1024).toLong())
        val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(context)
        simpleCache = SimpleCache(File(context.cacheDir, "media"), evict, databaseProvider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoScrollViewHolder {
        val videoScroller =
            LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
        Log.d("YT Shorts", "onCreateViewHolder Called")
        return VideoScrollViewHolder(videoScroller)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onBindViewHolder(holder: VideoScrollViewHolder, position: Int) {
        Log.d("YT Shorts", "Binding position: $position")
        val videoElement = vList[position]
        "@${videoElement.creator.handle}".also { holder.handle.text = it }
        holder.title.text = videoElement.submission.title
        holder.description.text = videoElement.submission.description
        holder.description.setOnClickListener {
            if (holder.description.text.length == 50) {
                holder.description.filters = arrayOf(InputFilter.LengthFilter(Int.MAX_VALUE))
            } else {
                holder.description.filters = arrayOf(InputFilter.LengthFilter(50))
            }
            holder.description.text = videoElement.submission.description
        }
        holder.comments.text = videoElement.comment.count.toString()
        holder.reaction.text = videoElement.reaction.count.toString()
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                holder.videoFrame.player = exoPlayer
                val mediaItem = MediaItem.fromUri(Uri.parse(videoElement.submission.mediaUrl))
                val httpDataSourceFactory =
                    DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
                val defaultDataSourceFactory =
                    DefaultDataSourceFactory(context, httpDataSourceFactory)
                val cacheDataSourceFactory = CacheDataSource.Factory()
                    .setCache(simpleCache)
                    .setUpstreamDataSourceFactory(defaultDataSourceFactory)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                val mediaSource = ProgressiveMediaSource
                    .Factory(cacheDataSourceFactory)
                    .createMediaSource(mediaItem)
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.playWhenReady = false
                exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                exoPlayer.prepare()
            }
    }

    override fun onViewAttachedToWindow(holder: VideoScrollViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.videoFrame.player?.apply {
            seekTo(0)
            play()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        Log.d("Yt Shorts", "Player Released onDetach")
        player.release()
        simpleCache.release()
    }

    override fun onViewDetachedFromWindow(holder: VideoScrollViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.videoFrame.player?.pause()
    }

    override fun getItemCount(): Int = vList.size

    class VideoScrollViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val handle: TextView = itemView.findViewById(R.id.handleText)
        val title: TextView = itemView.findViewById(R.id.videoTitleText)
        val description: TextView = itemView.findViewById(R.id.videoDescriptionText)
        val comments: TextView = itemView.findViewById(R.id.commentCount)
        val reaction: TextView = itemView.findViewById(R.id.reactionCount)
        val videoFrame: PlayerView = itemView.findViewById(R.id.videoFrame)
    }
}