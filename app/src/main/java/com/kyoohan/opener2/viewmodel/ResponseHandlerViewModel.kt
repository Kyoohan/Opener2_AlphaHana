package com.kyoohan.opener2.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.kyoohan.opener2.data.ChatMessage
import com.kyoohan.opener2.repository.ChatRepository
import com.kyoohan.opener2.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

/**
 * ChatViewModel의 응답 처리 로직을 담당하는 헬퍼 클래스
 */
class ResponseHandlerViewModel(
    private val viewModelScope: CoroutineScope,
    private val isKakaoLoggedIn: () -> Boolean,
    private val loginKakao: (Context) -> Unit
) {
    
    /**
     * Repository 응답 처리
     */
    fun handleResponse(
        response: String,
        context: Context,
        repository: ChatRepository,
        sessionId: String,
        onSessionUpdate: (String, List<ChatMessage>) -> Unit,
        onShowMapDialog: (String) -> Unit,
        onShowImagePicker: () -> Unit
    ) {
        println("🟡 ResponseHandler.handleResponse called")
        println("🟡 Response: [$response]")
        
        val decoded = ResponseCodec.decode(response)
        println("🟡 Decoded: $decoded")
        
        when (decoded) {
            is ChatResponse.Text -> {
                // 텍스트 응답은 이미 repository에 추가됨
                onSessionUpdate(sessionId, repository.messages.value)
            }
            
            is ChatResponse.NavigationLink -> {
                handleNavigationLink(decoded.deepLink, repository, sessionId, onSessionUpdate, onShowMapDialog)
            }
            
            is ChatResponse.ImagePickerRequest -> {
                onShowImagePicker()
            }
            
            is ChatResponse.KakaoSdkImageShare -> {
                handleKakaoSdkShare(decoded.imageUri, context, repository, sessionId, onSessionUpdate)
            }
            
            is ChatResponse.AndroidImageShare -> {
                handleAndroidShare(decoded.imageUri, context, repository, sessionId, onSessionUpdate)
            }
            
            is ChatResponse.KakaoMessageShare -> {
                // 카카오톡 메시지는 별도 다이얼로그에서 처리
                repository.addMessage(ChatMessage(
                    content = "카카오톡 공유 화면이 열렸습니다.",
                    isUser = false
                ))
                onSessionUpdate(sessionId, repository.messages.value)
            }
            
            is ChatResponse.PlayStoreLink -> {
                handlePlayStoreLink(decoded.packageName, decoded.appName, context, repository, sessionId, onSessionUpdate)
            }
            
            is ChatResponse.Error -> {
                repository.addMessage(ChatMessage(
                    content = "오류: ${decoded.message}",
                    isUser = false
                ))
                onSessionUpdate(sessionId, repository.messages.value)
            }
        }
    }
    
    /**
     * 길찾기 링크 처리
     */
    private fun handleNavigationLink(
        deepLink: String,
        repository: ChatRepository,
        sessionId: String,
        onSessionUpdate: (String, List<ChatMessage>) -> Unit,
        onShowMapDialog: (String) -> Unit
    ) {
        val message = ChatMessage(
            content = "🗺️ 네이버 지도 길찾기 링크가 생성되었습니다.\n\n[$deepLink]($deepLink)",
            isUser = false
        )
        repository.addMessage(message)
        onSessionUpdate(sessionId, repository.messages.value)
        onShowMapDialog(deepLink)
    }
    
    /**
     * Kakao SDK 이미지 공유
     */
    private fun handleKakaoSdkShare(
        imageUriString: String,
        context: Context,
        repository: ChatRepository,
        sessionId: String,
        onSessionUpdate: (String, List<ChatMessage>) -> Unit
    ) {
        if (!isKakaoLoggedIn()) {
            repository.addMessage(ChatMessage(
                content = "카카오톡으로 이미지를 전송하려면 카카오 로그인이 필요합니다.",
                isUser = false
            ))
            onSessionUpdate(sessionId, repository.messages.value)
            loginKakao(context)
            return
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            val uri = Uri.parse(imageUriString)
            val imageFile = FileUtils.getFileFromUri(context, uri)
            
            if (imageFile == null) {
                withContext(Dispatchers.Main) {
                    repository.addMessage(ChatMessage(
                        content = "이미지 파일을 읽을 수 없습니다.",
                        isUser = false
                    ))
                    onSessionUpdate(sessionId, repository.messages.value)
                }
                return@launch
            }
            
            withContext(Dispatchers.Main) {
                repository.addMessage(ChatMessage(
                    content = "이미지를 카카오 서버에 업로드하고 있습니다...",
                    isUser = false
                ))
                onSessionUpdate(sessionId, repository.messages.value)
                
                KakaoUtils.uploadAndSendImage(
                    context = context,
                    imagePath = imageFile.absolutePath,
                    onSuccess = {
                        repository.addMessage(ChatMessage(
                            content = "카카오톡 공유 화면이 열렸습니다. 전송할 대화방을 선택하세요.",
                            isUser = false
                        ))
                        onSessionUpdate(sessionId, repository.messages.value)
                        FileUtils.deleteTempFile(imageFile)
                    },
                    onFailure = { error ->
                        repository.addMessage(ChatMessage(
                            content = "카카오톡 이미지 공유에 실패했습니다: ${KakaoUtils.getErrorMessage(error)}",
                            isUser = false
                        ))
                        onSessionUpdate(sessionId, repository.messages.value)
                        FileUtils.deleteTempFile(imageFile)
                    }
                )
            }
        }
    }
    
    /**
     * Android 공유
     */
    private fun handleAndroidShare(
        imageUriString: String,
        context: Context,
        repository: ChatRepository,
        sessionId: String,
        onSessionUpdate: (String, List<ChatMessage>) -> Unit
    ) {
        try {
            val uri = Uri.parse(imageUriString)
            ShareUtils.shareImage(context, uri)
            
            repository.addMessage(ChatMessage(
                content = "공유 화면이 열렸습니다. 원하는 앱을 선택하세요.",
                isUser = false
            ))
            onSessionUpdate(sessionId, repository.messages.value)
        } catch (e: Exception) {
            repository.addMessage(ChatMessage(
                content = "이미지 공유에 실패했습니다: ${e.message}",
                isUser = false
            ))
            onSessionUpdate(sessionId, repository.messages.value)
        }
    }
    
    /**
     * 플레이스토어 링크 처리
     */
    private fun handlePlayStoreLink(
        packageName: String,
        appName: String,
        context: Context,
        repository: ChatRepository,
        sessionId: String,
        onSessionUpdate: (String, List<ChatMessage>) -> Unit
    ) {
        try {
            // 먼저 플레이스토어 앱으로 열기 시도
            val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                setPackage("com.android.vending")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            if (playStoreIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(playStoreIntent)
                repository.addMessage(ChatMessage(
                    content = "📱 $appName 앱의 플레이스토어 페이지가 열렸습니다.",
                    isUser = false
                ))
            } else {
                // 플레이스토어 앱이 없으면 웹 브라우저로 열기
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
                repository.addMessage(ChatMessage(
                    content = "📱 $appName 앱의 플레이스토어 웹페이지가 열렸습니다.",
                    isUser = false
                ))
            }
            onSessionUpdate(sessionId, repository.messages.value)
        } catch (e: Exception) {
            repository.addMessage(ChatMessage(
                content = "플레이스토어를 열 수 없습니다: ${e.message}",
                isUser = false
            ))
            onSessionUpdate(sessionId, repository.messages.value)
        }
    }
}

