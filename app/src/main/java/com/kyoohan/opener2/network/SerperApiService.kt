package com.kyoohan.opener2.network

import com.kyoohan.opener2.data.SerperRequest
import com.kyoohan.opener2.data.SerperResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SerperApiService {
    @POST("search")
    suspend fun search(
        @Header("X-API-KEY") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: SerperRequest
    ): Response<SerperResponse>
}


