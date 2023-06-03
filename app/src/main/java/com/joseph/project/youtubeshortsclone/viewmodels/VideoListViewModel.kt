package com.joseph.project.youtubeshortsclone.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joseph.project.youtubeshortsclone.model.Post
import com.joseph.project.youtubeshortsclone.networkrequest.RetrofitObject
import retrofit2.HttpException
import java.io.IOException

class VideoListViewModel : ViewModel() {
    private var pageNumber = 0
    private var videoItemList: MutableLiveData<ArrayList<Post>> = MutableLiveData()
    private var videoItem = ArrayList<Post>()
    private var networkError:MutableLiveData<Boolean> = MutableLiveData(false)
    private var position = 0

    init {
        Log.d("YT Shorts", "Init Called")
    }

    fun getVideoList(): MutableLiveData<ArrayList<Post>> {
        return videoItemList
    }
    fun getNetworkError(): MutableLiveData<Boolean> = networkError

    fun updateVideoList(position: Int, clickedPost: Post) {
        this.position = position
        videoItemList.value?.removeAt(position)
        videoItemList.value?.add(0, clickedPost)
    }

    fun resetList(){
        videoItemList.value?.get(0)?.let { videoItemList.value?.add(position+1, it) }
        videoItemList.value?.removeAt(0)
        Log.d("YT Shorts","List reset successfully")
    }

    suspend fun initialFetch(numberOfPages: Int = 1) {
        if (videoItem.size == 0) {
            for (i in 1..numberOfPages) {
                Log.d("YT Shorts", "Initial Fetch Called")
                try {
                    networkError.value=false
                    val response = RetrofitObject.api.getVideos(pageNumber++)
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.let {
                            if (videoItemList.value != null) {
                                videoItem = videoItemList.value!!
                            }
                            videoItem.addAll(it.data.posts)
                            videoItemList.value = videoItem
                        }
                    }
                } catch (e: IOException) {
                    Log.d("YT Shorts", "IO Exception: " + e.message + "\n" + e.toString())
                    networkError.postValue(true)
                    break
                } catch (e: HttpException) {
                    Log.d("YT Shorts", "HTTP Exception: " + e.message + "\n" + e.toString())
                    networkError.postValue(true)
                    break
                }
            }
        } else {
            Log.d("YT Shorts", "Initial Fetch declined")
        }
    }

    suspend fun fetchVideo() {
        try {
            Log.d("YT Shorts", "Fetch Called")
            networkError.value=false
            val response = RetrofitObject.api.getVideos(pageNumber++)
            if (response.isSuccessful && response.body() != null) {
                response.body()?.let {
                    videoItem = videoItemList.value!!
                    videoItem.addAll(it.data.posts)
                    videoItemList.value = videoItem
                }
            }
        } catch (e: IOException) {
            Log.d("YT Shorts", "IO Exception: " + e.message + "\n" + e.toString())
            networkError.postValue(true)
        } catch (e: HttpException) {
            Log.d("YT Shorts", "HTTP Exception: " + e.message + "\n" + e.toString())
            networkError.postValue(true)
        }
    }
}