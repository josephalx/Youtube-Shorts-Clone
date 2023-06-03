package com.joseph.project.youtubeshortsclone.networkrequest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {
    val api:HttpRequests by lazy {
        Retrofit.Builder()
            .baseUrl("https://internship-service.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HttpRequests::class.java)
    }
}