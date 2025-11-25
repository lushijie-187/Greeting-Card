package com.example.greetingcard.data.remote

import com.example.greetingcard.data.model.PexelsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    companion object {
        const val BASE_URL = "https://api.pexels.com/"
    }
    @GET("v1/curated")
    suspend fun getCuratedPhotos(
        @Header("Authorization") apiKey: String, // API Key 需要放在请求头里
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): PexelsResponse // 返回值是 PexelsResponse
}