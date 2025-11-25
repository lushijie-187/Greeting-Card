package com.example.greetingcard

data class Post(
    val imageResId: Int,
    val title: String,
    val userAvatarResId: Int,
    val userName: String,
    val likeCount: Int,
    val id: Int
)

sealed class ListItem {
    // 为每个列表项定义一个唯一的 ID，这对 DiffUtil 非常重要
    abstract val id: String
    data class PostItem(val post: Post) : ListItem() {
        override val id: String = post.id.toString()
    }
    object LoadingItem : ListItem() {
        // 加载项是唯一的，给它一个固定的 ID
        override val id: String = "loading_item"
    }
}
