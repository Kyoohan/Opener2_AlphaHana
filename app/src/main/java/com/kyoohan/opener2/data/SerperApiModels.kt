package com.kyoohan.opener2.data

import com.google.gson.annotations.SerializedName

// Serper API 요청 모델
data class SerperRequest(
    val q: String,                  // 검색 쿼리
    val gl: String = "kr",          // 국가 (대한민국)
    val hl: String = "ko",          // 언어 (한국어)
    val num: Int = 5                // 결과 개수
)

// Serper API 응답 모델
data class SerperResponse(
    val organic: List<SerperSearchResult>? = null,
    val answerBox: SerperAnswerBox? = null,
    val knowledgeGraph: SerperKnowledgeGraph? = null
)

// 검색 결과 항목
data class SerperSearchResult(
    val position: Int? = null,
    val title: String? = null,
    val link: String? = null,
    val snippet: String? = null,
    val date: String? = null
)

// Answer Box (즉시 답변)
data class SerperAnswerBox(
    val answer: String? = null,
    val snippet: String? = null,
    val title: String? = null
)

// Knowledge Graph
data class SerperKnowledgeGraph(
    val title: String? = null,
    val type: String? = null,
    val description: String? = null
)


