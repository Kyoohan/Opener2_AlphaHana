package com.kyoohan.opener2.network

import com.kyoohan.opener2.data.GeminiRequest
import com.kyoohan.opener2.data.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1/projects/yeollimi-dev/locations/us-central1/publishers/google/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}
