package com.kyoohan.opener2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyoohan.opener2.ui.theme.TextColor

@Composable
fun WelcomeScreen(
    onSuggestionClick: (String) -> Unit,
    fontSizeScale: Float = 1.0f,
    highContrastMode: Boolean = false,
    accentColorPreset: Int = 0,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Accent Color에 따라 그라디언트 선택
        val gradient = Brush.horizontalGradient(
            colors = when (accentColorPreset) {
                1 -> listOf(
                    com.kyoohan.opener2.ui.theme.AccentCoolStart,
                    com.kyoohan.opener2.ui.theme.AccentCoolMid1,
                    com.kyoohan.opener2.ui.theme.AccentCoolMid2,
                    com.kyoohan.opener2.ui.theme.AccentCoolEnd
                )
                2 -> listOf(
                    com.kyoohan.opener2.ui.theme.AccentPurpleStart,
                    com.kyoohan.opener2.ui.theme.AccentPurpleMid1,
                    com.kyoohan.opener2.ui.theme.AccentPurpleMid2,
                    com.kyoohan.opener2.ui.theme.AccentPurpleEnd
                )
                3 -> listOf(
                    com.kyoohan.opener2.ui.theme.AccentGreenStart,
                    com.kyoohan.opener2.ui.theme.AccentGreenMid1,
                    com.kyoohan.opener2.ui.theme.AccentGreenMid2,
                    com.kyoohan.opener2.ui.theme.AccentGreenEnd
                )
                else -> listOf(
                    com.kyoohan.opener2.ui.theme.AccentWarmStart,
                    com.kyoohan.opener2.ui.theme.AccentWarmMid1,
                    com.kyoohan.opener2.ui.theme.AccentWarmMid2,
                    com.kyoohan.opener2.ui.theme.AccentWarmEnd
                )
            }
        )
        
        Text(
            text = "열림이에 오신 것을\u00A0환영합니다",
            fontSize = (32 * fontSizeScale).sp,
            fontWeight = if (highContrastMode) FontWeight.ExtraBold else FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = if (highContrastMode) com.kyoohan.opener2.ui.theme.HighContrastTextColor else Color.Unspecified,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            style = if (highContrastMode) {
                androidx.compose.ui.text.TextStyle(color = com.kyoohan.opener2.ui.theme.HighContrastTextColor)
            } else {
                androidx.compose.ui.text.TextStyle(brush = gradient)
            }
        )
    }
}

// 추천 액션 제거됨
