package com.example.greetingcard.data.repository

import android.util.Log
import com.example.greetingcard.BuildConfig
import com.example.greetingcard.data.model.Post
import com.example.greetingcard.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PostRepository {
    private val apiService = RetrofitClient.apiService

    // 使用 withContext 切换到 IO 线程执行网络请求
    suspend fun getPosts(page: Int, count: Int): List<Post> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCuratedPhotos(
                apiKey = BuildConfig.PEXELS_API_KEY, // 从 BuildConfig 安全地获取 Key
                page = page,
                perPage = count
            )
            // 将 PexelsPhoto 映射成我们的 Post 模型
            response.photos.map { pexelsPhoto ->
                Post(
                    id = pexelsPhoto.id,
                    title = "Photo by ${pexelsPhoto.photographer}",
                    imageUrl = pexelsPhoto.src.medium,
                    userName = pexelsPhoto.photographer,
                    userAvatarUrl = pexelsPhoto.src.tiny, // 用最小的图做头像
                    likeCount = pexelsPhoto.id % 1000,
                )
            }
        } catch (e: Exception) {
            // 在实际项目中，这里应该处理异常，例如返回一个空列表或抛出一个自定义错误
            Log.e("PostRepository", "Failed to fetch posts", e)
            emptyList<Post>()
        }
    }

    suspend fun deletePost(post: Post) {
        println("Simulating delete for post ID: ${post.id}")
    }
}