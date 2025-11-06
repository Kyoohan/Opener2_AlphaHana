package com.kyoohan.opener2.utils

import java.util.Locale

/**
 * 이 파일은 더 이상 사용되지 않습니다.
 * Vertex AI 모델이 자동으로 길찾기 의도를 감지하고 딥링크를 생성합니다.
 */
@Deprecated("Use Vertex AI model for intent detection and deep link generation")
data class NavigationIntent(
    val transport_mode: String = "public",
    val destination: Destination,
    val origin: Origin = Origin(kind = "current_location")
)

@Deprecated("Use Vertex AI model for intent detection and deep link generation")
data class Destination(
    val query: String
)

@Deprecated("Use Vertex AI model for intent detection and deep link generation")
data class Origin(
    val kind: String
)

@Deprecated("Use Vertex AI model for intent detection and deep link generation")
object NavigationIntentParser {
    fun parse(message: String): NavigationIntent? {
        val normalized = message.lowercase(Locale.getDefault())

        val keywords = listOf("길안내", "길 안내", "길찾기", "길 찾기", "가는 길", "루트", "네비", "내비")
        val hasNavKeyword = keywords.any { normalized.contains(it.lowercase(Locale.getDefault())) }
        if (!hasNavKeyword) return null

        // Extract destination by removing typical command phrases
        val cleaned = message
            .replace("까지", "")
            .replace("로", "")
            .replace("으로", "")
            .replace("한번", "")
            .replace("좀", "")
            .replace("요", "")
            .replace(Regex("(길\\s?안내|길\\s?찾기|안내|경로|가(?:줘|자|요)?|알려줘|찾아줘|찾아줘요|가줘)"), "")
            .trim()

        val destination = cleaned.ifBlank { null } ?: cleaned
        if (destination.isBlank()) return null

        return NavigationIntent(
            transport_mode = "public",
            destination = Destination(query = destination.trim()),
            origin = Origin(kind = "current_location")
        )
    }
}


