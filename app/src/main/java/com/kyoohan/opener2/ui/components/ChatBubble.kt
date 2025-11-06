package com.kyoohan.opener2.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.kyoohan.opener2.data.ChatMessage
import com.kyoohan.opener2.ui.theme.*

@Composable
fun ChatBubble(
    message: ChatMessage,
    fontSizeScale: Float = 1.0f,
    highContrastMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (highContrastMode) {
        if (message.isUser) HighContrastUserBubbleColor else HighContrastModelBubbleColor
    } else {
        if (message.isUser) UserBubbleColor else ModelBubbleColor
    }
    
    val textColor = if (highContrastMode) HighContrastTextColor else TextColor
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (message.isUser) 20.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 20.dp
                    )
                )
                .background(bubbleColor)
                .then(
                    if (highContrastMode) {
                        Modifier.border(
                            width = 2.dp,
                            color = HighContrastBorderColor,
                            shape = RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp,
                                bottomStart = if (message.isUser) 20.dp else 4.dp,
                                bottomEnd = if (message.isUser) 4.dp else 20.dp
                            )
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(16.dp)
        ) {
            Column {
                // 이미지 표시 (있는 경우)
                if (message.imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(message.imageUri),
                        contentDescription = "첨부된 이미지",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    if (message.content.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // 텍스트 표시 (있는 경우)
                if (message.content.isNotBlank()) {
                    if (message.isUser) {
                        Text(
                            text = message.content,
                            color = textColor,
                            fontSize = (16 * fontSizeScale).sp,
                            lineHeight = (24 * fontSizeScale).sp
                        )
                    } else {
                        SimpleMarkdownText(
                            text = message.content,
                            color = textColor,
                            fontSize = 16 * fontSizeScale
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingBubble(
    fontSizeScale: Float = 1.0f,
    highContrastMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (highContrastMode) HighContrastModelBubbleColor else ModelBubbleColor
    val textColor = if (highContrastMode) HighContrastTextColor else TextColor
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(bubbleColor)
                .then(
                    if (highContrastMode) {
                        Modifier.border(
                            width = 2.dp,
                            color = HighContrastBorderColor,
                            shape = RoundedCornerShape(20.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(4.dp),
                    color = textColor,
                    strokeWidth = if (highContrastMode) 3.dp else 2.dp
                )
                Text(
                    text = "답변 생성 중...",
                    color = textColor,
                    fontSize = (16 * fontSizeScale).sp,
                    fontWeight = if (highContrastMode) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}