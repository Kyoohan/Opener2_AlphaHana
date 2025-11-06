package com.kyoohan.opener2.data

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null  // 첨부된 이미지 URI
)

enum class MessageType {
    USER, AI, LOADING
}

