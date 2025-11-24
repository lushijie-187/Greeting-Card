package com.example.greetingcard

data class Post(
    val imageResId: Int, // 图片资源ID (暂时用本地图片代替网络图片)
    val title: String,
    val userAvatarResId: Int,
    val userName: String,
    val likeCount: Int
)
