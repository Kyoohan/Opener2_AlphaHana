package com.kyoohan.opener2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.kyoohan.opener2.ui.components.ChatBubble
import com.kyoohan.opener2.ui.components.LoadingBubble
import com.kyoohan.opener2.ui.components.MapDialog
import com.kyoohan.opener2.ui.components.KakaoDialog
import com.kyoohan.opener2.ui.components.FriendPickerDialog
import com.kyoohan.opener2.ui.components.ImagePickerDialog
import com.kyoohan.opener2.ui.components.MessageInput
import com.kyoohan.opener2.ui.components.TestMarkdown
import com.kyoohan.opener2.ui.components.SpeechStatusBubble
import com.kyoohan.opener2.ui.components.TabBar
import com.kyoohan.opener2.ui.components.WelcomeScreen
import com.kyoohan.opener2.ui.components.FontSizeSettingsDialog
import com.kyoohan.opener2.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onSuggestionClick: (String) -> Unit = {}
) {
    val sessions by viewModel.sessions.collectAsState()
    val activeSessionId by viewModel.activeSessionId.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentMessage by viewModel.currentMessage.collectAsState()
    val isMapLoading by viewModel.isMapLoading.collectAsState()
    val showMapDialog by viewModel.showMapDialog.collectAsState()
    val showKakaoDialog by viewModel.showKakaoDialog.collectAsState()
    val kakaoMessage by viewModel.kakaoMessage.collectAsState()
    val isKakaoLoggedIn by viewModel.isKakaoLoggedIn.collectAsState()
    val showFriendPicker by viewModel.showFriendPicker.collectAsState()
    val friendsList by viewModel.friendsList.collectAsState()
    val isFriendsLoading by viewModel.isFriendsLoading.collectAsState()
    val pendingFriendMessage by viewModel.pendingFriendMessage.collectAsState()
    val showImagePicker by viewModel.showImagePicker.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val speechError by viewModel.speechError.collectAsState()
    val showSettings by viewModel.showSettings.collectAsState()
    val showMoreMenu by viewModel.showMoreMenu.collectAsState()
    val fontSizeScale by viewModel.fontSizeScale.collectAsState()
    val actualFontSizeScale by viewModel.actualFontSizeScale.collectAsState()
    val highContrastMode by viewModel.highContrastMode.collectAsState()
    val accentColorPreset by viewModel.accentColorPreset.collectAsState()

    val listState = rememberLazyListState()
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    
    // 음성 인식 권한 요청 launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startSpeechRecognition()
        } else {
            viewModel.clearSpeechError()
        }
    }
    
    // 이미지 선택 launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // KakaoDialog가 열려있으면 카카오 이미지로, 아니면 일반 이미지로
            if (showKakaoDialog) {
                viewModel.selectKakaoImage(it)
            } else {
                viewModel.onImageSelected(context, it)
            }
        }
    }
    
    // 갤러리 권한 요청 launcher (Android 13+)
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        }
    }
    
    // 카메라로 찍은 사진 임시 저장용 URI
    val cameraImageUri = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf<Uri?>(null)
    }
    
    // 카메라 launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri.value?.let { uri ->
                viewModel.onImageSelected(context, uri)
            }
        }
    }
    
    // 카메라 권한 요청 launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 카메라 사진 저장을 위한 임시 URI 생성
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                java.io.File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            )
            cameraImageUri.value = uri
            cameraLauncher.launch(uri)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { viewModel.showSettings() }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "설정",
                    tint = if (highContrastMode) 
                        com.kyoohan.opener2.ui.theme.HighContrastTextColor 
                    else 
                        com.kyoohan.opener2.ui.theme.TextColor
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg -> 
                    ChatBubble(
                        message = msg, 
                        fontSizeScale = actualFontSizeScale,
                        highContrastMode = highContrastMode
                    ) 
                }
                if (isLoading || isMapLoading) { 
                    item { 
                        LoadingBubble(
                            fontSizeScale = actualFontSizeScale,
                            highContrastMode = highContrastMode
                        ) 
                    } 
                }
            }
            
            // 메시지가 없을 때 환영 화면을 중앙에 고정
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    WelcomeScreen(
                        onSuggestionClick = { /* 추천 액션 제거됨 */ },
                        fontSizeScale = actualFontSizeScale,
                        highContrastMode = highContrastMode,
                        accentColorPreset = 0
                    )
                }
            }
        }
        
        // 음성 인식 상태 표시
        SpeechStatusBubble(
            isListening = isListening,
            errorMessage = speechError,
            modifier = Modifier.fillMaxWidth()
        )

        MessageInput(
            message = currentMessage,
            onMessageChange = { viewModel.updateCurrentMessage(it) },
            onSendClick = {
                viewModel.sendMessage()
                keyboard?.hide()
                scope.launch {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(messages.lastIndex)
                    }
                }
            },
            onMicClick = { 
                // 권한 체크 후 요청
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    viewModel.startSpeechRecognition()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                }
            },
            onStopClick = { viewModel.stopSpeechRecognition() },
            onImageClick = {
                // 갤러리 권한 확인 후 이미지 선택
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        imagePickerLauncher.launch("image/*")
                    } else {
                        galleryPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        imagePickerLauncher.launch("image/*")
                    } else {
                        galleryPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            },
            onCameraClick = {
                // 카메라 권한 확인 후 카메라 실행
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    val uri = androidx.core.content.FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        java.io.File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                    )
                    cameraImageUri.value = uri
                    cameraLauncher.launch(uri)
                } else {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            },
            onImageRemove = { viewModel.removeSelectedImage() },
            selectedImageUri = selectedImageUri,
            isListening = isListening,
            highContrastMode = highContrastMode,
            showMoreMenu = showMoreMenu,
            onMoreMenuToggle = { viewModel.toggleMoreMenu() },
            fontSizeScale = actualFontSizeScale,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .align(Alignment.CenterHorizontally)
                .imePadding()
                .padding(bottom = 12.dp)
        )
    }
    
    // 지도 다이얼로그
    if (showMapDialog) {
        MapDialog(
            onOpenMap = { viewModel.openMap(context) },
            onDismiss = { viewModel.dismissMapDialog() },
            onCancel = { viewModel.handleMapDialogCancel() }
        )
    }
    
    // 카카오톡 다이얼로그
    if (showKakaoDialog) {
        val kakaoImageUri by viewModel.kakaoImageUri.collectAsState()
        val isImageOnly = kakaoMessage.isEmpty()  // 빈 메시지면 이미지 전용 모드
        KakaoDialog(
            initialMessage = kakaoMessage,
            isLoggedIn = isKakaoLoggedIn,
            imageUri = kakaoImageUri,
            isImageOnly = isImageOnly,
            onSendMessage = { messageText, imageUri -> 
                viewModel.sendKakaoMessageWithImage(context, messageText, imageUri) 
            },
            onImageSelect = { 
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        imagePickerLauncher.launch("image/*")
                    } else {
                        galleryPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        imagePickerLauncher.launch("image/*")
                    } else {
                        galleryPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            },
            onImageRemove = { viewModel.removeKakaoImage() },
            onLogin = { viewModel.loginKakao(context) },
            onDismiss = { viewModel.dismissKakaoDialog() },
            fontSizeScale = actualFontSizeScale
        )
    }
    
    // 친구 선택 다이얼로그
    if (showFriendPicker) {
        FriendPickerDialog(
            friends = friendsList,
            message = pendingFriendMessage,
            onFriendSelected = { friend -> viewModel.sendMessageToFriend(context, friend) },
            onDismiss = { viewModel.dismissFriendPicker() },
            isLoading = isFriendsLoading
        )
    }
    
    // 이미지 선택 다이얼로그
    if (showImagePicker) {
        ImagePickerDialog(
            onSelectImage = {
                // 갤러리 권한 확인 후 실행
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        imagePickerLauncher.launch("image/*")
                        // dismissImagePicker는 이미지 선택 후 onImageSelected에서 호출됨
                    } else {
                        galleryPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        // dismissImagePicker는 이미지 선택 후 onImageSelected에서 호출됨
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        imagePickerLauncher.launch("image/*")
                        // dismissImagePicker는 이미지 선택 후 onImageSelected에서 호출됨
                    } else {
                        galleryPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        // dismissImagePicker는 이미지 선택 후 onImageSelected에서 호출됨
                    }
                }
            },
            onDismiss = { viewModel.dismissImagePicker() }
        )
    }
    
    // 글자 크기 설정 다이얼로그
    if (showSettings) {
        FontSizeSettingsDialog(
            currentFontSizeScale = fontSizeScale,
            currentHighContrastMode = highContrastMode,
            currentAccentColorPreset = accentColorPreset,
            onFontSizeChange = { scale -> viewModel.updateFontSizeScale(scale) },
            onHighContrastModeChange = { enabled -> viewModel.updateHighContrastMode(enabled) },
            onAccentColorChange = { preset -> viewModel.updateAccentColorPreset(preset) },
            onDismiss = { viewModel.dismissSettings() }
        )
    }
}
