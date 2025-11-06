package com.kyoohan.opener2.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TestMarkdown(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SimpleMarkdownText(
            text = "일반 텍스트입니다.",
            color = MaterialTheme.colorScheme.onSurface
        )
        
        SimpleMarkdownText(
            text = "**굵은 글씨 테스트**입니다.",
            color = MaterialTheme.colorScheme.onSurface
        )
        
        SimpleMarkdownText(
            text = "*기울임 글씨* 테스트입니다.",
            color = MaterialTheme.colorScheme.onSurface
        )
        
        SimpleMarkdownText(
            text = "`코드` 테스트입니다.",
            color = MaterialTheme.colorScheme.onSurface
        )
        
        SimpleMarkdownText(
            text = "[링크](https://example.com) 테스트입니다.",
            color = MaterialTheme.colorScheme.onSurface
        )
        
        SimpleMarkdownText(
            text = "복합 테스트: **굵은 글씨**와 *기울임 글씨* 그리고 `코드`가 함께 있습니다.",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
