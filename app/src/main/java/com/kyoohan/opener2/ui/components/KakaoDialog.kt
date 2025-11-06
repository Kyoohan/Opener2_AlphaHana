package com.kyoohan.opener2.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun KakaoDialog(
    initialMessage: String,
    isLoggedIn: Boolean,
    imageUri: String? = null,
    onSendMessage: (String, String?) -> Unit,
    onImageSelect: () -> Unit,
    onImageRemove: () -> Unit,
    onLogin: () -> Unit,
    onDismiss: () -> Unit,
    fontSizeScale: Float = 1.0f,
    isImageOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf(initialMessage) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💬",
                    fontSize = (48 * fontSizeScale).sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = if (isLoggedIn) {
                        if (isImageOnly) "카카오톡으로 사진 전송" else "카카오톡으로 메시지 전송"
                    } else {
                        "카카오 로그인이 필요해요"
                    },
                    fontSize = (20 * fontSizeScale).sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 텍스트 입력 필드 (이미지 전용 모드가 아닐 때만 표시)
                if (!isImageOnly) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        label = { Text("메시지 내용", fontSize = (12 * fontSizeScale).sp) },
                        placeholder = { Text("전송할 메시지를 입력하세요", fontSize = (14 * fontSizeScale).sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = (16 * fontSizeScale).sp
                        ),
                        minLines = 2,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
                
                // 이미지 첨부 버튼 및 미리보기
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 이미지 첨부 버튼 (갤러리 아이콘)
                    if (imageUri == null) {
                        OutlinedButton(
                            onClick = onImageSelect,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = "갤러리에서 선택")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("갤러리")
                        }
                    } else {
                        // 이미지 미리보기
                        Box(modifier = Modifier.weight(1f).height(80.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "첨부된 이미지",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = onImageRemove,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "이미지 제거",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                
                Text(
                    text = if (isLoggedIn) 
                        "전송 버튼을 누르면 카카오톡 공유 화면이 열립니다" 
                    else 
                        "카카오 로그인 후 메시지를 전송할 수 있습니다",
                    fontSize = (14 * fontSizeScale).sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("취소", fontSize = (14 * fontSizeScale).sp)
                    }
                    
                    Button(
                        onClick = { 
                            if (isLoggedIn) {
                                onSendMessage(messageText.trim(), imageUri)
                            } else {
                                onLogin()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = if (isImageOnly) {
                            imageUri != null
                        } else {
                            messageText.trim().isNotEmpty() || imageUri != null
                        }
                    ) {
                        Text(if (isLoggedIn) "전송" else "로그인", fontSize = (14 * fontSizeScale).sp)
                    }
                }
            }
        }
    }
}

