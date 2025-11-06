package com.kyoohan.opener2.data

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    val contents: List<Content>,
    val systemInstruction: SystemInstruction? = null,
    val generationConfig: GenerationConfig? = null,
    val tools: List<Tool>? = null  // Google Search Grounding
)

data class SystemInstruction(
    val parts: List<Part>
)

data class GenerationConfig(
    val maxOutputTokens: Int? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

data class Content(
    val parts: List<Part>,
    val role: String? = null  // "user" 또는 "model"
)

data class Part(
    val text: String? = null,
    @SerializedName("inline_data")
    val inlineData: InlineData? = null
)

data class InlineData(
    @SerializedName("mime_type")
    val mimeType: String,  // "image/jpeg", "image/png" 등
    val data: String  // base64로 인코딩된 이미지 데이터
)

// Google Search Grounding Tool (최신 API 형식)
data class Tool(
    @SerializedName("google_search")
    val googleSearch: GoogleSearch? = null
)

class GoogleSearch {
    // 빈 객체로 사용 (기본 설정 사용)
}

data class GeminiResponse(
    val candidates: List<Candidate>,
    val usageMetadata: UsageMetadata? = null
)

data class Candidate(
    val content: Content,
    val groundingMetadata: GroundingMetadata? = null
)

// Grounding Metadata (검색 결과 정보)
data class GroundingMetadata(
    val webSearchQueries: List<String>? = null,
    val groundingChunks: List<GroundingChunk>? = null
)

data class GroundingChunk(
    val web: WebChunk? = null
)

data class WebChunk(
    val uri: String? = null,
    val title: String? = null
)

data class UsageMetadata(
    val promptTokenCount: Int? = null,
    val candidatesTokenCount: Int? = null,
    val totalTokenCount: Int? = null
)

data class GeminiError(
    val error: ErrorDetail
)

data class ErrorDetail(
    val code: Int,
    val message: String,
    val status: String
)

