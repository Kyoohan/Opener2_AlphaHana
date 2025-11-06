package com.kyoohan.opener2.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    private const val SERPER_BASE_URL = "https://google.serper.dev/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val geminiRetrofit = Retrofit.Builder()
        .baseUrl(GEMINI_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val serperRetrofit = Retrofit.Builder()
        .baseUrl(SERPER_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val geminiApiService: GeminiApiService = geminiRetrofit.create(GeminiApiService::class.java)
    val vertexTunedApiService: VertexTunedApiService = geminiRetrofit.create(VertexTunedApiService::class.java)
    val serperApiService: SerperApiService = serperRetrofit.create(SerperApiService::class.java)
}

