package com.kyoohan.opener2.network

import com.kyoohan.opener2.data.GeminiRequest
import com.kyoohan.opener2.data.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VertexTunedApiService {
    @POST("v1beta/tunedModels/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") modelId: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}













