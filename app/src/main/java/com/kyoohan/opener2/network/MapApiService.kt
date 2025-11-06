package com.kyoohan.opener2.network

import com.kyoohan.opener2.data.MaplinkRequest
import com.kyoohan.opener2.data.MaplinkResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface MapApi {
    @POST("/v1/maplink")
    suspend fun maplink(@Body req: MaplinkRequest): MaplinkResponse
}

object MapApiClient {
    // Cloud Shell Server URL (Vertex AI를 통한 자연어 처리 + 딥링크 생성)
    private const val BASE_URL = "https://yeollimi-backend-5knyei6kxa-uc.a.run.app/"

    private val client = okhttp3.OkHttpClient.Builder()
        .callTimeout(java.time.Duration.ofSeconds(12))
        .build()

    val api: MapApi = retrofit2.Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()
        .create(MapApi::class.java)
}


















