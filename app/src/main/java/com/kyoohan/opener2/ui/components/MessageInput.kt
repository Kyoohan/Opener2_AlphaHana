package com.kyoohan.opener2.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.kyoohan.opener2.ui.theme.*

@Composable
fun MessageInput(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit,
    onStopClick: () -> Unit,
    onImageClick: () -> Unit,
    onCameraClick: () -> Unit,
    onImageRemove: () -> Unit,
    selectedImageUri: String? = null,
    isListening: Boolean = false,
    highContrastMode: Boolean = false,
    showMoreMenu: Boolean = false,
    onMoreMenuToggle: () -> Unit = {},
    fontSizeScale: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    val surfaceColor = if (highContrastMode) HighContrastSurfaceColor else SurfaceColor
    val borderColor = if (highContrastMode) HighContrastBorderColor else Color(0xFF5F6368)
    val borderWidth = if (highContrastMode) 2.dp else 1.dp
    val textColor = if (highContrastMode) HighContrastTextColor else TextColor
    val secondaryColor = if (highContrastMode) HighContrastSecondaryTextColor else SecondaryTextColor
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // 더보기 메뉴 (입력 필드 위에 표시)
        if (showMoreMenu) {
            MoreMenu(
                onImageClick = onImageClick,
                onCameraClick = onCameraClick,
                selectedImageUri = selectedImageUri,
                highContrastMode = highContrastMode,
                onDismiss = onMoreMenuToggle
            )
        }
        
        // 이미지 미리보기 (선택된 경우)
        if (selectedImageUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(surfaceColor)
                        .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "선택된 이미지",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    // X 버튼 (이미지 제거)
                    IconButton(
                        onClick = onImageRemove,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "이미지 제거",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
        
        // 입력 필드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(surfaceColor)
                .border(borderWidth, borderColor, RoundedCornerShape(28.dp))
                .padding(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = onMessageChange,
                    placeholder = {
                        Text(
                            text = if (selectedImageUri != null) "이미지에 대해 질문해보세요!" else "여기에 궁금한 것을 물어봐주세요!",
                            color = secondaryColor,
                            fontWeight = if (highContrastMode) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = (14 * fontSizeScale).sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = (16 * fontSizeScale).sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    maxLines = 7,
                    minLines = 1
                )
                
                // 더보기 버튼 (사진/카메라)
                IconButton(
                    onClick = onMoreMenuToggle,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "사진선택, 카메라 메뉴 열기",
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // 음성 인식 버튼
                IconButton(
                    onClick = if (isListening) onStopClick else onMicClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isListening) "음성 인식 중지" else "음성 입력",
                        tint = if (isListening) Color.Red else textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // 전송 버튼
                IconButton(
                    onClick = onSendClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "전송",
                        tint = if (message.isNotBlank() || selectedImageUri != null) textColor else secondaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MoreMenu(
    onImageClick: () -> Unit,
    onCameraClick: () -> Unit,
    selectedImageUri: String?,
    highContrastMode: Boolean,
    onDismiss: () -> Unit
) {
    val surfaceColor = if (highContrastMode) HighContrastSurfaceColor else SurfaceColor
    val borderColor = if (highContrastMode) HighContrastBorderColor else Color(0xFF5F6368)
    val borderWidth = if (highContrastMode) 2.dp else 1.dp
    val textColor = if (highContrastMode) HighContrastTextColor else TextColor
    val secondaryColor = if (highContrastMode) HighContrastSecondaryTextColor else SecondaryTextColor
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(surfaceColor)
                .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            // 사진 선택 버튼
            MoreMenuButton(
                icon = Icons.Default.Image,
                label = "사진 선택",
                onClick = onImageClick,
                textColor = if (selectedImageUri != null) Color(0xFF4285F4) else textColor,
                highContrastMode = highContrastMode
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 카메라 버튼
            MoreMenuButton(
                icon = Icons.Default.CameraAlt,
                label = "카메라",
                onClick = onCameraClick,
                textColor = textColor,
                highContrastMode = highContrastMode
            )
        }
    }
}

@Composable
fun MoreMenuButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    textColor: Color,
    highContrastMode: Boolean
) {
    val buttonColor = if (highContrastMode) Color(0xFF2A2A2A) else Color(0xFF3A3A3A)
    val borderColor = if (highContrastMode) HighContrastBorderColor else Color(0xFF5F6368)
    val borderWidth = if (highContrastMode) 2.dp else 1.dp
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(buttonColor)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (highContrastMode) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

