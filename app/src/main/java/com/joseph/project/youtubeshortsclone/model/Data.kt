package com.joseph.project.youtubeshortsclone.model

data class Data(
    val offset: Int,
    val page: Int,
    val posts: List<Post>
)