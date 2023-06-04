package com.joseph.project.youtubeshortsclone.fragments

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.joseph.project.youtubeshortsclone.R
import com.joseph.project.youtubeshortsclone.databinding.VideoFragmentBinding
import com.joseph.project.youtubeshortsclone.model.Post
import com.joseph.project.youtubeshortsclone.recyclerviewadapters.VideoScrollerAdapter
import com.joseph.project.youtubeshortsclone.viewmodels.VideoListViewModel
import java.io.File

class VideoFragment : Fragment(R.layout.video_fragment) {
    private var videoFragmentBinding: VideoFragmentBinding? = null
    private val binding get() = videoFragmentBinding!!
    private var ytTag: String = "YT Shorts"
    private var videoList = ArrayList<Post>()
    private lateinit var videoListViewModel: VideoListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        videoFragmentBinding = VideoFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SourceLockedOrientationActivity", "UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as AppCompatActivity).requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val videoAdapter = VideoScrollerAdapter(videoList, requireContext())

        videoListViewModel = ViewModelProvider(requireActivity())[VideoListViewModel::class.java]
        videoListViewModel.getVideoList().observe(viewLifecycleOwner) {
            Log.d(ytTag, "Item: " + it[0].toString())
            if (it.size == videoList.size) {
                binding.videoScroller.currentItem = 1
                binding.videoScrollProgressBar.visibility = ProgressBar.GONE
            } else {
                videoList.clear()
                videoList.addAll(it)
                videoAdapter.notifyItemRangeChanged(0, it.size)
                binding.videoScrollProgressBar.visibility = ProgressBar.GONE
            }
        }

        videoListViewModel.getNetworkError().observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(
                    requireContext(),
                    "Network error try after some time",
                    Toast.LENGTH_LONG
                ).show()
                binding.videoScrollProgressBar.visibility = ProgressBar.GONE
            }
        }

        binding.videoScroller.apply {
            adapter = videoAdapter
        }
        binding.videoScroller.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE || state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    if (binding.videoScroller.currentItem == (videoList.size - 1)) {
                        fetchVideo()
                    }
                }
            }
        })
    }


    fun fetchVideo() {
        binding.videoScrollProgressBar.visibility = ProgressBar.VISIBLE
        lifecycleScope.launchWhenCreated {
            videoListViewModel.fetchVideo()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(ytTag, "On Pause Called")
        videoListViewModel.resetList()
        videoListViewModel.getNetworkError().removeObservers(viewLifecycleOwner)
        videoListViewModel.getVideoList().removeObservers(viewLifecycleOwner)
        (activity as AppCompatActivity).requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(ytTag, "onDestroy called in Video Fragment")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.window?.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        (activity as AppCompatActivity).supportActionBar?.show()
        val cache = File(requireActivity().cacheDir, "media")
        if (cache.delete()) {
            Log.d("YT Shorts", "Cache Deleted")
        }

        binding.videoScroller.adapter = null
        videoFragmentBinding = null
    }
}