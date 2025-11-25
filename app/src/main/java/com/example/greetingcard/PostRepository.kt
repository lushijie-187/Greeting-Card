// file: PostRepository.kt
package com.example.greetingcard

import kotlinx.coroutines.delay

// 这是一个单例，因为在整个应用中我们通常只需要一个数据仓库实例
object PostRepository {

    // 模拟网络/数据库操作，所以使用 suspend 关键字
    // 这强制调用者必须在协程中调用此函数
    suspend fun getPosts(page: Int, count: Int): List<Post> {
        // 模拟 1 秒的网络延迟
        delay(1000)
        return PostGenerator.generateRandomPosts(count)
    }

    // 同样，模拟删除操作也可能有延迟
    suspend fun deletePost(post: Post) {
        // 模拟 200 毫秒的 API 请求延迟
        delay(200)
        // 在实际应用中，这里会调用 API 或操作数据库
        println("Post '${post.title}' deleted from the backend.")
    }
}
