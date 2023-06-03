package com.joseph.project.youtubeshortsclone.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joseph.project.youtubeshortsclone.R
import com.joseph.project.youtubeshortsclone.databinding.HomeFragmentBinding
import com.joseph.project.youtubeshortsclone.model.Post
import com.joseph.project.youtubeshortsclone.recyclerviewadapters.VideoListAdapter
import com.joseph.project.youtubeshortsclone.viewmodels.VideoListViewModel

class HomeFragment : Fragment(R.layout.home_fragment) {
    private var fragmentBinding: HomeFragmentBinding? = null
    private val binding get() = fragmentBinding!!
    private var videoList = ArrayList<Post>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = HomeFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        val videoFragment = VideoFragment()
        val videoListModel = ViewModelProvider(requireActivity())[VideoListViewModel::class.java]

        Log.d("YT Shorts", "Called onCreate")

//        binding.switchFragment.setOnClickListener {
//            activity?.supportFragmentManager!!.beginTransaction().apply {
//                replace(R.id.flFragment, videoFragment)
//                addToBackStack("videoFragment")
//                commit()
//            }
//        }

        val itemAdapter = VideoListAdapter(videoList)
        itemAdapter.setClickListener(object : VideoListAdapter.OnClickListener {
            override fun onClick(position: Int, model: Post) {
                videoListModel.updateVideoList(position, model)
                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    replace(R.id.flFragment, videoFragment)
                    addToBackStack("videoFragment")
                    commit()
                }
            }
        })

        videoListModel.getVideoList().observe(viewLifecycleOwner) {
            Log.d("YT Shorts", "success")
            videoList.clear()
            videoList.addAll(it)
            itemAdapter.notifyItemRangeChanged(0, it.size)
            binding.progressBar.visibility = ProgressBar.GONE
        }
        videoListModel.getNetworkError().observe(viewLifecycleOwner){
            if(it){
                binding.progressBar.visibility=ProgressBar.GONE
                Toast.makeText(requireContext(),"Network error try after some time",Toast.LENGTH_LONG).show()
            }
        }

        binding.recyclerView.apply {
            layoutManager =
                GridLayoutManager(context, resources.getInteger(R.integer.video_card_span))
            adapter = itemAdapter
        }
        var firstCall = true
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && firstCall) {
                    modelFetch(videoListModel)
                    firstCall=false
                }
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    firstCall=false
                }
            }
        })
        initFetch(videoListModel, resources.getInteger(R.integer.initialFetch))
    }

    private fun modelFetch(videoListViewModel: VideoListViewModel) {
        binding.progressBar.visibility = ProgressBar.VISIBLE
        Log.d("YT Shorts", "model fetch")
        lifecycleScope.launchWhenCreated {
            Log.d("YT Shorts","Scope Called")
            videoListViewModel.fetchVideo()
        }
    }

    private fun initFetch(videoListViewModel: VideoListViewModel, pageNumber: Int) {
        binding.progressBar.visibility = ProgressBar.VISIBLE
        lifecycleScope.launchWhenCreated {
            videoListViewModel.initialFetch(pageNumber)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        fragmentBinding = null
    }
}