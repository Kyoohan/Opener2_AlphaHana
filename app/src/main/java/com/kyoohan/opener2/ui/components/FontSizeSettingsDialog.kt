package com.kyoohan.opener2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kyoohan.opener2.ui.theme.*
import com.kyoohan.opener2.utils.AccessibilityUtils

@Composable
fun FontSizeSettingsDialog(
    currentFontSizeScale: Float,
    currentHighContrastMode: Boolean,
    currentAccentColorPreset: Int,
    onFontSizeChange: (Float) -> Unit,
    onHighContrastModeChange: (Boolean) -> Unit,
    onAccentColorChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var fontSizeScale by remember { mutableFloatStateOf(currentFontSizeScale) }
    var highContrastMode by remember { mutableStateOf(currentHighContrastMode) }
    val context = LocalContext.current
    val actualFontSizeScale = fontSizeScale  // Dialog에서 사용할 폰트 스케일
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceColor,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "접근성 설정",
                        fontSize = (24 * actualFontSizeScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = TextColor
                        )
                    }
                }
                
                HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.3f))
                
                // 글자 크기 설정
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "글자 크기",
                        fontSize = (16 * actualFontSizeScale).sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextColor
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "작게",
                            fontSize = (12 * actualFontSizeScale).sp,
                            color = SecondaryTextColor
                        )
                        Slider(
                            value = fontSizeScale,
                            onValueChange = { 
                                fontSizeScale = it
                                onFontSizeChange(it)
                            },
                            valueRange = 1.0f..2.0f,
                            steps = 9, // 1.0, 1.1, 1.2, ..., 2.0 (10단계)
                            modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                        )
                        Text(
                            text = "크게",
                            fontSize = (12 * actualFontSizeScale).sp,
                            color = SecondaryTextColor
                        )
                    }
                    Text(
                        text = "현재: ${String.format("%.1f", fontSizeScale)}배",
                        fontSize = (14 * actualFontSizeScale).sp,
                        color = SecondaryTextColor,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                
                HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.3f))
                
                // 고대비 모드 토글
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "고대비 모드",
                            fontSize = (16 * actualFontSizeScale).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextColor
                        )
                        Text(
                            text = "더 명확한 대비로 가독성 향상",
                            fontSize = (12 * actualFontSizeScale).sp,
                            color = SecondaryTextColor
                        )
                    }
                    Switch(
                        checked = highContrastMode,
                        onCheckedChange = { 
                            highContrastMode = it
                            onHighContrastModeChange(it)
                        }
                    )
                }
                
                HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.3f))
                
                // TalkBack 설정
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TalkBack 설정",
                        fontSize = if (actualFontSizeScale > 1.7f) (14 * actualFontSizeScale).sp else (16 * actualFontSizeScale).sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextColor,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            AccessibilityUtils.openTalkBackSettings(context)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (highContrastMode) HighContrastBorderColor else AccentWarmStart // 청록색 사용
                        )
                    ) {
                        Text(
                            text = "설정 열기",
                            fontSize = (14 * actualFontSizeScale).sp,
                            color = if (highContrastMode) androidx.compose.ui.graphics.Color.Black else androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
                
                HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.3f))
                
                // 미리보기 텍스트 (실제 스케일 적용, 잘리지 않도록 padding 추가)
                Text(
                    text = "미리보기: 안녕하세요! 열림이입니다.",
                    fontSize = (16 * actualFontSizeScale).sp,
                    color = TextColor,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 8.dp)
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

