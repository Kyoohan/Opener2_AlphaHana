package com.kyoohan.opener2.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kyoohan.opener2.ui.theme.AccentWarmStart
import com.kyoohan.opener2.ui.theme.SecondaryTextColor
import com.kyoohan.opener2.ui.theme.SurfaceColor
import com.kyoohan.opener2.ui.theme.TextColor

@Composable
fun ApiKeysDialog(
    currentGeminiKey: String,
    currentVertexKey: String,
    onGeminiKeySave: (String) -> Unit,
    onGeminiKeyClear: () -> Unit,
    onVertexKeySave: (String) -> Unit,
    onVertexKeyClear: () -> Unit,
    onDismiss: () -> Unit
) {
    var geminiKey by remember { mutableStateOf(currentGeminiKey) }
    var vertexKey by remember { mutableStateOf(currentVertexKey) }
    var geminiVisible by remember { mutableStateOf(false) }
    var vertexVisible by remember { mutableStateOf(false) }

    LaunchedEffect(currentGeminiKey) { geminiKey = currentGeminiKey }
    LaunchedEffect(currentVertexKey) { vertexKey = currentVertexKey }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceColor,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = AccentWarmStart
                        )
                        Text(
                            text = "API 키 설정",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColor,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = TextColor
                        )
                    }
                }

                Text(
                    text = "앱에서 Gemini 및 Vertex AI 서비스를 사용하려면 API 키가 필요합니다. 각 키를 입력하고 저장하세요.",
                    fontSize = 14.sp,
                    color = SecondaryTextColor
                )

                HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.3f))

                ApiKeySection(
                    title = "Gemini API 키",
                    placeholder = "API 키를 입력해 주세요.",
                    value = geminiKey,
                    isVisible = geminiVisible,
                    onValueChange = { geminiKey = it },
                    onVisibilityToggle = { geminiVisible = !geminiVisible },
                    onSave = { onGeminiKeySave(geminiKey.trim()) },
                    onClear = {
                        geminiKey = ""
                        onGeminiKeyClear()
                    }
                )

                HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.3f))

                ApiKeySection(
                    title = "Vertex API 키",
                    placeholder = "API 키를 입력해 주세요.",
                    value = vertexKey,
                    isVisible = vertexVisible,
                    onValueChange = { vertexKey = it },
                    onVisibilityToggle = { vertexVisible = !vertexVisible },
                    onSave = { onVertexKeySave(vertexKey.trim()) },
                    onClear = {
                        vertexKey = ""
                        onVertexKeyClear()
                    }
                )
            }
        }
    }
}

@Composable
private fun ApiKeySection(
    title: String,
    placeholder: String,
    value: String,
    isVisible: Boolean,
    onValueChange: (String) -> Unit,
    onVisibilityToggle: () -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextColor
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = SecondaryTextColor,
                    fontSize = 14.sp
                )
            },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextColor,
                unfocusedTextColor = TextColor,
                focusedBorderColor = AccentWarmStart,
                unfocusedBorderColor = SecondaryTextColor.copy(alpha = 0.4f),
                focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                cursorColor = AccentWarmStart
            ),
            trailingIcon = {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isVisible) "키 숨기기" else "키 보기",
                        tint = TextColor
                    )
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onSave,
                enabled = value.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentWarmStart,
                    contentColor = androidx.compose.ui.graphics.Color.Black
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "저장")
            }

            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AccentWarmStart
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = AccentWarmStart
                )
            ) {
                Text(text = "지우기")
            }
        }
    }
}

