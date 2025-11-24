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
        return Post(
            imageResId = randomImage,
            title = "随机帖子 #${Random.nextInt(1000, 9999)}", // 标题为随机数
            userAvatarResId = randomAvatar,
            userName = "随机用户 #${Random.nextInt(100, 999)}",
            likeCount = Random.nextInt(0, 2000)
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