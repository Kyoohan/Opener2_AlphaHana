package com.kyoohan.opener2.ui.theme

import androidx.compose.ui.graphics.Color

// Dark theme colors inspired by the HTML design
val BackgroundColor = Color(0xFF202124)
val BackgroundGradientCenter = Color(0xFF292A2D)
val SurfaceColor = Color(0xFF303134)
val TextColor = Color(0xFFE8EAED)
val SecondaryTextColor = Color(0xFF9AA0A6)
val UserBubbleColor = Color(0xFF3C4043)
val ModelBubbleColor = Color(0xFF282A2D)

// 고대비 모드 색상 (WCAG AAA 기준 - 저시력자용)
val HighContrastBackgroundColor = Color(0xFF000000)      // 순수 검정
val HighContrastSurfaceColor = Color(0xFF000000)         // 순수 검정 (배경과 동일)
val HighContrastTextColor = Color(0xFFFFFF00)            // 밝은 노란색 (저시력자에게 가장 잘 보임)
val HighContrastSecondaryTextColor = Color(0xFFFFFF00)   // 밝은 노란색
val HighContrastUserBubbleColor = Color(0xFF1A1A1A)      // 매우 어두운 회색
val HighContrastModelBubbleColor = Color(0xFF0A0A0A)     // 거의 검정
val HighContrastBorderColor = Color(0xFFFFFF00)          // 밝은 노란색 테두리

// Gradient colors
val GradientStartDefault = Color(0x99E6E6E6)
val GradientEndDefault = Color(0x99C8C8C8)
val GradientStartFocus = Color(0xB3A855F7)
val GradientEndFocus = Color(0xB37828C8)

// Accent Color Presets (환영 메시지 그라디언트용)
// Warm (기본) - 청록색 계열로 변경
val AccentWarmStart = Color(0xFF03DAC5)      // 밝은 청록색 (teal_200)
val AccentWarmMid1 = Color(0xFF02C9B3)       // 중간 밝기 청록색
val AccentWarmMid2 = Color(0xFF01B8A1)       // 중간 청록색
val AccentWarmEnd = Color(0xFF018786)        // 진한 청록색 (teal_700)

// Cool (차가운 톤)
val AccentCoolStart = Color(0xFF8BE9FD)
val AccentCoolMid1 = Color(0xFF50FA7B)
val AccentCoolMid2 = Color(0xFF5DADE2)
val AccentCoolEnd = Color(0xFFBD93F9)

// Purple (보라)
val AccentPurpleStart = Color(0xFFD4A5FF)
val AccentPurpleMid1 = Color(0xFFA855F7)
val AccentPurpleMid2 = Color(0xFF9333EA)
val AccentPurpleEnd = Color(0xFF7828C8)

// Green (초록) - 청록색 계열로 변경
val AccentGreenStart = Color(0xFF03DAC5)      // 밝은 청록색 (teal_200)
val AccentGreenMid1 = Color(0xFF02C9B3)       // 중간 밝기 청록색
val AccentGreenMid2 = Color(0xFF01B8A1)       // 중간 청록색
val AccentGreenEnd = Color(0xFF018786)        // 진한 청록색 (teal_700)

// Legacy colors (keeping for compatibility)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)