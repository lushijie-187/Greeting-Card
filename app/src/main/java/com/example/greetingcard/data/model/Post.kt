package com.example.greetingcard.data.model
data class PexelsPhoto(
    val id: Int,
    val photographer: String,
    val src: PhotoSource
)
data class PhotoSource(
    val medium: String, // 我们用中等尺寸的图片
    val tiny: String // 可以用作头像
)
// Pexels API 的响应体
data class PexelsResponse(
    val photos: List<PexelsPhoto>,
    val page: Int,
    val per_page: Int,
    val next_page: String?
)
data class Post(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val userName: String,
    val userAvatarUrl: String,
    val likeCount: Int
)

sealed class ListItem {
    abstract val id: String
    data class PostItem(val post: Post) : ListItem() {
        override val id: String = post.id.toString()
    }
    object LoadingItem : ListItem() {
        override val id: String = "loading_item"
    }
}
