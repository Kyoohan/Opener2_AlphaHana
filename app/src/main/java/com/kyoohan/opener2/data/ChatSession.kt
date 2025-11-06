package com.kyoohan.opener2.data

import java.util.UUID

data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val messages: List<ChatMessage> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun getDisplayTitle(): String {
        return if (title.length > 20) "${title.take(17)}..." else title
    }
}