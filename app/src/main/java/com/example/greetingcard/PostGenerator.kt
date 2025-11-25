package com.example.greetingcard

import kotlin.random.Random

object PostGenerator {

    public fun generateRandomPosts(count: Int): List<Post> {
        val postList = mutableListOf<Post>()
        repeat(count) {
            postList.add(generateRandomPost())
        }
        return postList
    }

    private fun generateRandomPost(): Post {
        val randomImage = imagePool.random()
        val randomAvatar = avatarPool.random()
        val titleId = Random.nextInt(1000, 9999)
        val userId = Random.nextInt(100, 999)
        return Post(
            imageResId = randomImage,
            title = "随机帖子 #${titleId}",
            userAvatarResId = randomAvatar,
            userName = "随机用户 #${userId}",
            likeCount = Random.nextInt(0, 2000),
            id = userId * 10000 + titleId
        )
    }

    private val imagePool = listOf(
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_manage,
        android.R.drawable.ic_menu_compass,
        android.R.drawable.ic_menu_directions,
        android.R.drawable.ic_menu_search
    )
    private val avatarPool = listOf(
        android.R.drawable.sym_def_app_icon,
        android.R.drawable.star_on,
        android.R.drawable.btn_star_big_off
    )
}