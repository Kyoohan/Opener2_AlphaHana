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
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val context = LocalContext.current
    
    val annotatedString = buildAnnotatedString {
        // 간단한 마크다운 처리
        processSimpleMarkdown(text, color)
    }
    
    // 디버깅을 위한 로그 (개발 중에만 사용)
    println("MarkdownText Debug - Input text: $text")
    println("MarkdownText Debug - Contains **: ${text.contains("**")}")
    
    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
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

private fun androidx.compose.ui.text.AnnotatedString.Builder.processSimpleMarkdown(text: String, color: Color) {
    var processedText = text
    
    println("processSimpleMarkdown Debug - Original text: $text")
    
    // 링크 처리 [text](url)
    val linkRegex = Regex("""\[([^\]]+)\]\(([^)]+)\)""")
    val linkMatches = linkRegex.findAll(processedText).toList().reversed()
    for (match in linkMatches) {
        val linkText = match.groupValues[1]
        val url = match.groupValues[2]
        processedText = processedText.replace(match.value, "§LINK§$linkText§$url§")
        println("processSimpleMarkdown Debug - Found link: $linkText -> $url")
    }
    
    // 굵은 글씨 처리 **text** 또는 __text__
    val boldRegex = Regex("""\*\*(.*?)\*\*|__(.*?)__""")
    val boldMatches = boldRegex.findAll(processedText).toList().reversed()
    for (match in boldMatches) {
        val content = match.groupValues[1].ifEmpty { match.groupValues[2] }
        processedText = processedText.replace(match.value, "§BOLD§$content§")
        println("processSimpleMarkdown Debug - Found bold: $content")
    }
    
    println("processSimpleMarkdown Debug - After bold processing: $processedText")
    
    // 기울임 글씨 처리 *text* 또는 _text_ (굵은 글씨가 아닌 경우)
    val italicRegex = Regex("""(?<!\*)\*([^*]+)\*(?!\*)|(?<!_)_([^_]+)_(?!_)""")
    val italicMatches = italicRegex.findAll(processedText).toList().reversed()
    for (match in italicMatches) {
        val content = match.groupValues[1].ifEmpty { match.groupValues[2] }
        processedText = processedText.replace(match.value, "§ITALIC§$content§")
    }
    
    // 인라인 코드 처리 `code`
    val codeRegex = Regex("""`([^`]+)`""")
    val codeMatches = codeRegex.findAll(processedText).toList().reversed()
    for (match in codeMatches) {
        val content = match.groupValues[1]
        processedText = processedText.replace(match.value, "§CODE§$content§")
    }
    
    // 처리된 텍스트를 렌더링
    val parts = processedText.split("§")
    var i = 0
    while (i < parts.size) {
        when {
            i + 1 < parts.size && parts[i + 1] == "BOLD" -> {
                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = color
                )) {
                    append(parts[i + 2])
                }
                i += 3
            }
            i + 1 < parts.size && parts[i + 1] == "ITALIC" -> {
                withStyle(style = SpanStyle(
                    fontStyle = FontStyle.Italic,
                    color = color
                )) {
                    append(parts[i + 2])
                }
                i += 3
            }
            i + 1 < parts.size && parts[i + 1] == "CODE" -> {
                withStyle(style = SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    background = Color.Gray.copy(alpha = 0.2f),
                    color = color
                )) {
                    append(parts[i + 2])
                }
                i += 3
            }
            i + 1 < parts.size && parts[i + 1] == "LINK" && i + 3 < parts.size -> {
                val linkText = parts[i + 2]
                val url = parts[i + 3]
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
                i += 4
            }
            else -> {
                // 일반 텍스트
                if (parts[i].isNotEmpty()) {
                    withStyle(style = SpanStyle(color = color)) {
                        append(parts[i])
                    }
                }
                i++
            }
        }
    }
}