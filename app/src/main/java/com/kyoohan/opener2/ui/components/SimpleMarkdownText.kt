package com.kyoohan.opener2.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri

@Composable
fun SimpleMarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: Float = 16f
) {
    val context = LocalContext.current
    
    val annotatedString = buildAnnotatedString {
        // 매우 간단한 마크다운 처리
        var remainingText = text
        var currentIndex = 0
        
        while (currentIndex < remainingText.length) {
            // 굵은 글씨 **text** 찾기
            val boldStart = remainingText.indexOf("**", currentIndex)
            if (boldStart != -1) {
                val boldEnd = remainingText.indexOf("**", boldStart + 2)
                if (boldEnd != -1) {
                    // 굵은 글씨 이전의 일반 텍스트
                    if (boldStart > currentIndex) {
                        withStyle(style = SpanStyle(color = color)) {
                            append(remainingText.substring(currentIndex, boldStart))
                        }
                    }
                    
                    // 굵은 글씨
                    val boldText = remainingText.substring(boldStart + 2, boldEnd)
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )) {
                        append(boldText)
                    }
                    
                    currentIndex = boldEnd + 2
                    continue
                }
            }
            
            // 기울임 글씨 *text* 찾기 (굵은 글씨가 아닌 경우)
            val italicStart = remainingText.indexOf("*", currentIndex)
            if (italicStart != -1 && 
                (italicStart == 0 || remainingText[italicStart - 1] != '*') &&
                italicStart + 1 < remainingText.length && remainingText[italicStart + 1] != '*') {
                
                val italicEnd = remainingText.indexOf("*", italicStart + 1)
                if (italicEnd != -1 && 
                    (italicEnd == remainingText.length - 1 || remainingText[italicEnd + 1] != '*')) {
                    
                    // 기울임 글씨 이전의 일반 텍스트
                    if (italicStart > currentIndex) {
                        withStyle(style = SpanStyle(color = color)) {
                            append(remainingText.substring(currentIndex, italicStart))
                        }
                    }
                    
                    // 기울임 글씨
                    val italicText = remainingText.substring(italicStart + 1, italicEnd)
                    withStyle(style = SpanStyle(
                        fontStyle = FontStyle.Italic,
                        color = color
                    )) {
                        append(italicText)
                    }
                    
                    currentIndex = italicEnd + 1
                    continue
                }
            }
            
            // 인라인 코드 `code` 찾기
            val codeStart = remainingText.indexOf("`", currentIndex)
            if (codeStart != -1) {
                val codeEnd = remainingText.indexOf("`", codeStart + 1)
                if (codeEnd != -1) {
                    // 코드 이전의 일반 텍스트
                    if (codeStart > currentIndex) {
                        withStyle(style = SpanStyle(color = color)) {
                            append(remainingText.substring(currentIndex, codeStart))
                        }
                    }
                    
                    // 코드
                    val codeText = remainingText.substring(codeStart + 1, codeEnd)
                    withStyle(style = SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = Color.Gray.copy(alpha = 0.2f),
                        color = color
                    )) {
                        append(codeText)
                    }
                    
                    currentIndex = codeEnd + 1
                    continue
                }
            }
            
            // 링크 [text](url) 찾기
            val linkStart = remainingText.indexOf("[", currentIndex)
            if (linkStart != -1) {
                val linkTextEnd = remainingText.indexOf("]", linkStart + 1)
                if (linkTextEnd != -1 && 
                    linkTextEnd + 1 < remainingText.length && 
                    remainingText[linkTextEnd + 1] == '(') {
                    
                    val urlEnd = remainingText.indexOf(")", linkTextEnd + 2)
                    if (urlEnd != -1) {
                        // 링크 이전의 일반 텍스트
                        if (linkStart > currentIndex) {
                            withStyle(style = SpanStyle(color = color)) {
                                append(remainingText.substring(currentIndex, linkStart))
                            }
                        }
                        
                        // 링크
                        val linkText = remainingText.substring(linkStart + 1, linkTextEnd)
                        val url = remainingText.substring(linkTextEnd + 2, urlEnd)
                        
                        pushStringAnnotation(
                            tag = "URL",
                            annotation = url
                        )
                        withStyle(style = SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        )) {
                            append(linkText)
                        }
                        pop()
                        
                        currentIndex = urlEnd + 1
                        continue
                    }
                }
            }
            
            // 더 이상 마크다운이 없으면 나머지 텍스트 추가
            withStyle(style = SpanStyle(color = color)) {
                append(remainingText.substring(currentIndex))
            }
            break
        }
    }
    
    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = TextStyle(
            fontSize = fontSize.sp,
            lineHeight = (fontSize * 1.5f).sp,
            color = color
        ),
        onClick = { offset ->
            // 링크 클릭 처리
            annotatedString.getStringAnnotations(
                tag = "URL",
                start = offset,
                end = offset
            ).firstOrNull()?.let { annotation ->
                val url = annotation.item
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        }
    )
}









