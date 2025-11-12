package com.kyoohan.opener2.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kyoohan.opener2.BuildConfig
import com.kyoohan.opener2.data.ChatMessage
import com.kyoohan.opener2.data.ChatSession
import com.kyoohan.opener2.repository.ChatRepository
import com.kyoohan.opener2.utils.*
import android.content.Intent
import android.net.Uri
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repositories = mutableMapOf<String, ChatRepository>()
    private val preferencesManager = PreferencesManager(application)
    private val speechToTextUtils = SpeechToTextUtils(application)
    
    // 응답 처리 헬퍼
    private val responseHandler = ResponseHandlerViewModel(
        viewModelScope = viewModelScope,
        isKakaoLoggedIn = { _isKakaoLoggedIn.value },
        loginKakao = { context -> loginKakao(context) }
    )
    
    private val _sessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val sessions: StateFlow<List<ChatSession>> = _sessions.asStateFlow()
    
    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId: StateFlow<String?> = _activeSessionId.asStateFlow()
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // API 키는 SharedPreferences에서 로드하거나 사용자가 입력
    // 하드코딩된 키는 제거되었습니다 - 보안을 위해 local.properties 또는 앱 내 설정에서 관리하세요
    private val _apiKey = MutableStateFlow(preferencesManager.getApiKey() ?: "")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()
    
    // Vertex AI tuned model API key - 학습된 딥링크 생성 모델 접근용
    // BuildConfig를 통해 기본값 제공 (공유된 학습 모델 사용 가능)
    // local.properties에서 vertex.api.key를 설정하면 해당 값 사용, 없으면 기본값 사용
    private val _vertexApiKey = MutableStateFlow(
        preferencesManager.getVertexApiKey() ?: BuildConfig.VERTEX_API_KEY
    )
    val vertexApiKey: StateFlow<String> = _vertexApiKey.asStateFlow()
    
    private val _currentMessage = MutableStateFlow("")
    val currentMessage: StateFlow<String> = _currentMessage.asStateFlow()
    
    // 길찾기 관련 상태
    private val _isMapLoading = MutableStateFlow(false)
    val isMapLoading: StateFlow<Boolean> = _isMapLoading.asStateFlow()
    
    private val _showMapDialog = MutableStateFlow(false)
    val showMapDialog: StateFlow<Boolean> = _showMapDialog.asStateFlow()
    
    private val _mapUrl = MutableStateFlow("")
    val mapUrl: StateFlow<String> = _mapUrl.asStateFlow()
    
    private val _pendingNavigationQuery = MutableStateFlow("")
    private val _pendingSessionId = MutableStateFlow<String?>(null)
    
    // STT 관련 상태
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()
    
    private val _speechError = MutableStateFlow("")
    val speechError: StateFlow<String> = _speechError.asStateFlow()
    
    // 카카오톡 관련 상태
    private val _showKakaoDialog = MutableStateFlow(false)
    val showKakaoDialog: StateFlow<Boolean> = _showKakaoDialog.asStateFlow()
    
    private val _kakaoMessage = MutableStateFlow("")
    val kakaoMessage: StateFlow<String> = _kakaoMessage.asStateFlow()
    
    private val _kakaoImageUri = MutableStateFlow<String?>(null)
    val kakaoImageUri: StateFlow<String?> = _kakaoImageUri.asStateFlow()
    
    private val _isKakaoLoggedIn = MutableStateFlow(false)
    val isKakaoLoggedIn: StateFlow<Boolean> = _isKakaoLoggedIn.asStateFlow()
    
    // 친구 선택 관련 상태
    private val _showFriendPicker = MutableStateFlow(false)
    val showFriendPicker: StateFlow<Boolean> = _showFriendPicker.asStateFlow()
    
    private val _friendsList = MutableStateFlow<List<Friend>>(emptyList())
    val friendsList: StateFlow<List<Friend>> = _friendsList.asStateFlow()
    
    private val _isFriendsLoading = MutableStateFlow(false)
    val isFriendsLoading: StateFlow<Boolean> = _isFriendsLoading.asStateFlow()
    
    private val _pendingFriendMessage = MutableStateFlow("")
    val pendingFriendMessage: StateFlow<String> = _pendingFriendMessage.asStateFlow()
    
    private val _targetFriendName = MutableStateFlow<String?>(null)
    
    // 이미지 전송 관련 상태
    private val _showImagePicker = MutableStateFlow(false)
    val showImagePicker: StateFlow<Boolean> = _showImagePicker.asStateFlow()
    
    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri: StateFlow<String?> = _selectedImageUri.asStateFlow()
    
    // 이미지 전송 의도 저장 (이미지 선택 후 자동 공유를 위해)
    private val _isPendingImageShare = MutableStateFlow(false)
    private val _pendingShareMessage = MutableStateFlow("")  // 원래 메시지 저장
    
    // 설정 관련 상태
    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()
    
    private val _showMoreMenu = MutableStateFlow(false)
    val showMoreMenu: StateFlow<Boolean> = _showMoreMenu.asStateFlow()
    
    private val _showApiKeyDialog = MutableStateFlow(false)
    val showApiKeyDialog: StateFlow<Boolean> = _showApiKeyDialog.asStateFlow()
    
    private val _fontSizeScale = MutableStateFlow(preferencesManager.getFontSizeScale())
    val fontSizeScale: StateFlow<Float> = _fontSizeScale.asStateFlow()
    
    // UI에 표시할 실제 폰트 크기 스케일 (1.0 = 원래 1.2배)
    val actualFontSizeScale: StateFlow<Float> = _fontSizeScale.map { uiScale ->
        uiScale * 1.2f  // UI scale을 실제 scale로 변환
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, preferencesManager.getActualFontSizeScale())
    
    private val _highContrastMode = MutableStateFlow(preferencesManager.getHighContrastMode())
    val highContrastMode: StateFlow<Boolean> = _highContrastMode.asStateFlow()
    
    private val _accentColorPreset = MutableStateFlow(preferencesManager.getAccentColorPreset())
    val accentColorPreset: StateFlow<Int> = _accentColorPreset.asStateFlow()
    
    fun updateApiKey(apiKey: String) {
        _apiKey.value = apiKey
        preferencesManager.saveApiKey(apiKey)
    }
    
    fun updateVertexApiKey(apiKey: String) {
        _vertexApiKey.value = apiKey.ifBlank { BuildConfig.VERTEX_API_KEY }
        if (apiKey.isBlank()) {
            preferencesManager.clearVertexApiKey()
        } else {
            preferencesManager.saveVertexApiKey(apiKey)
        }
    }
    
    init {
        createNewSession("New Chat")
        checkKakaoLoginStatus()
    }
    
    /**
     * 카카오 로그인 상태 확인
     */
    private fun checkKakaoLoginStatus() {
        val context = getApplication<Application>()
        KakaoUtils.isKakaoLoggedIn(context) { isLoggedIn ->
            _isKakaoLoggedIn.value = isLoggedIn
        }
    }
    
    fun updateCurrentMessage(message: String) {
        _currentMessage.value = message
    }
    
    fun createNewSession(title: String = "New Chat"): String {
        val newSession = ChatSession(title = title)
        val newRepository = ChatRepository()
        repositories[newSession.id] = newRepository
        
        _sessions.value = _sessions.value + newSession
        selectSession(newSession.id)
        return newSession.id
    }
    
    fun selectSession(sessionId: String) {
        _activeSessionId.value = sessionId
        val repository = repositories[sessionId]
        if (repository != null) {
            viewModelScope.launch {
                repository.messages.collect { messages ->
                    _messages.value = messages
                }
            }
            viewModelScope.launch {
                repository.isLoading.collect { loading ->
                    _isLoading.value = loading
                }
            }
            // Update session with current messages
            updateSessionMessages(sessionId, repository.messages.value)
        }
    }
    
    fun closeSession(sessionId: String) {
        if (_sessions.value.size <= 1) {
            // 마지막 탭도 실제로 닫히도록: 새 세션을 만든 뒤 기존 세션 제거
            val newId = createNewSession("New Chat")
            repositories.remove(sessionId)
            _sessions.value = _sessions.value.filter { it.id != sessionId }
            _activeSessionId.value = newId
            return
        }

        repositories.remove(sessionId)
        _sessions.value = _sessions.value.filter { it.id != sessionId }

        if (_activeSessionId.value == sessionId) {
            val remainingSessions = _sessions.value
            if (remainingSessions.isNotEmpty()) {
                selectSession(remainingSessions.last().id)
            } else {
                // 예외적으로 모두 닫힌 경우 대비
                val newId = createNewSession("New Chat")
                _activeSessionId.value = newId
            }
        }
    }
    
    private fun updateSessionMessages(sessionId: String, messages: List<ChatMessage>) {
        _sessions.value = _sessions.value.map { session ->
            if (session.id == sessionId) {
                val newTitle = if (messages.isNotEmpty() && session.title == "New Chat") {
                    messages.first().content.take(30).replace("\n", " ")
                } else {
                    session.title
                }
                session.copy(messages = messages, title = newTitle, lastUpdated = System.currentTimeMillis())
            } else {
                session
            }
        }
    }
    
    fun sendMessage() {
        val message = _currentMessage.value.trim()
        val apiKey = _apiKey.value.trim()
        val vertexKey = _vertexApiKey.value.trim()
        val currentSessionId = _activeSessionId.value
        val imageUri = _selectedImageUri.value
        val context = getApplication<Application>()
        
        // 메시지나 이미지 중 하나는 있어야 함
        if ((message.isEmpty() && imageUri == null) || apiKey.isEmpty() || currentSessionId == null) {
            return
        }
        
        // 입력창 및 이미지 즉시 초기화 (사용자 경험 개선)
        _currentMessage.value = ""
        _selectedImageUri.value = null
        
        val repository = repositories[currentSessionId]
        if (repository != null) {
            viewModelScope.launch {
                val result = repository.sendMessage(
                    message = message,
                    apiKey = apiKey,
                    vertexApiKey = vertexKey,
                    context = context,
                    imageUri = imageUri
                )
                    
                    result.onSuccess { response ->
                        // 간소화된 응답 처리 - ResponseHandler 사용
                        when (val decoded = ResponseCodec.decode(response)) {
                            is ChatResponse.ImagePickerRequest -> {
                                // 프롬프트 메시지에 "카카오톡"이 포함되어 있으면 카카오 이미지 전송 모드
                                if (decoded.message.contains("카카오톡")) {
                                    // 카카오 이미지 전송 다이얼로그 열기 (이미지만 전송 가능)
                                    _kakaoMessage.value = ""
                                    _showKakaoDialog.value = true
                                } else {
                                    // 일반 이미지 선택
                                    _isPendingImageShare.value = true
                                    _pendingShareMessage.value = message  // 원래 메시지 저장
                                    _showImagePicker.value = true
                                }
                            }
                            is ChatResponse.NavigationLink -> {
                                _mapUrl.value = decoded.deepLink
                                val linkMessage = ChatMessage(
                                    content = "🗺️ 네이버 지도 길찾기 링크가 생성되었습니다.\n\n[${decoded.deepLink}](${decoded.deepLink})",
                                    isUser = false
                                )
                                repository.addMessage(linkMessage)
                                updateSessionMessages(currentSessionId, repository.messages.value)
                                _showMapDialog.value = true
                            }
                            is ChatResponse.KakaoMessageShare -> {
                                _kakaoMessage.value = decoded.message
                                _showKakaoDialog.value = true
                            }
                            else -> {
                                // KakaoSdkImageShare, AndroidImageShare, Text 등은 ResponseHandler가 처리
                                responseHandler.handleResponse(
                                    response = response,
                                    context = context,
                                    repository = repository,
                                    sessionId = currentSessionId,
                                    onSessionUpdate = ::updateSessionMessages,
                                    onShowMapDialog = { url -> 
                                        _mapUrl.value = url
                                        _showMapDialog.value = true
                                    },
                                    onShowImagePicker = {
                                        _isPendingImageShare.value = true
                                        _showImagePicker.value = true
                                    }
                                )
                            }
                        }
                    }
                    
                    result.onFailure { error ->
                        println("ERROR: ViewModel error: ${error.message}")
                        repository.addMessage(ChatMessage(
                            content = "오류가 발생했습니다: ${error.message}",
                            isUser = false
                        ))
                        updateSessionMessages(currentSessionId, repository.messages.value)
                    }
                }
            }
        }
    
    // 이전 복잡한 코드 제거됨 - 아래는 다른 함수들
    
    fun openMap(context: Context) {
        val url = _mapUrl.value
        if (url.isNotEmpty()) {
            openNaverMap(context, url)
            _showMapDialog.value = false
        }
    }
    
    fun dismissMapDialog() {
        _showMapDialog.value = false
    }
    
    fun handleMapDialogCancel() {
        val query = _pendingNavigationQuery.value
        val sessionId = _pendingSessionId.value
        
        if (query.isNotEmpty() && sessionId != null) {
            val repository = repositories[sessionId]
            if (repository != null) {
                viewModelScope.launch {
                    // 일반 AI 응답으로 처리
                    val context = getApplication<Application>()
                    val result = repository.sendMessage(
                        message = query,
                        apiKey = _apiKey.value,
                        vertexApiKey = _vertexApiKey.value,
                        context = context,
                        imageUri = null
                    )
                    result.onFailure { error ->
                        println("DEBUG: ViewModel error on cancel: ${error.message}")
                        val errorMessage = ChatMessage(
                            content = "오류가 발생했습니다: ${error.message}",
                            isUser = false
                        )
                        repository.addMessage(errorMessage)
                    }
                    updateSessionMessages(sessionId, repository.messages.value)
                }
            }
        }
        
        // 저장된 쿼리와 세션 ID 초기화
        _pendingNavigationQuery.value = ""
        _pendingSessionId.value = null
    }
    
    fun clearMessages() {
        val currentSessionId = _activeSessionId.value
        if (currentSessionId != null) {
            repositories[currentSessionId]?.clearMessages()
            updateSessionMessages(currentSessionId, emptyList())
        }
    }
    
    fun clearApiKey() {
        _apiKey.value = ""
        preferencesManager.clearApiKey()
    }
    
    fun clearVertexApiKey() {
        _vertexApiKey.value = BuildConfig.VERTEX_API_KEY
        preferencesManager.clearVertexApiKey()
    }
    
    /**
     * 음성 인식 시작
     */
    fun startSpeechRecognition() {
        if (!speechToTextUtils.hasPermission()) {
            _speechError.value = "음성 인식 권한이 필요합니다. 설정에서 권한을 허용해주세요."
            // 3초 후 에러 메시지 자동 삭제
            viewModelScope.launch {
                delay(3000)
                _speechError.value = ""
            }
            return
        }
        
        _isListening.value = true
        _speechError.value = ""
        
        speechToTextUtils.startListening(
            onResult = { recognizedText ->
                _isListening.value = false
                _currentMessage.value = recognizedText
            },
            onError = { error ->
                _isListening.value = false
                _speechError.value = error
                // 3초 후 에러 메시지 자동 삭제
                viewModelScope.launch {
                    delay(3000)
                    _speechError.value = ""
                }
            }
        )
    }
    
    /**
     * 음성 인식 중지
     */
    fun stopSpeechRecognition() {
        speechToTextUtils.stopListening()
        _isListening.value = false
    }
    
    /**
     * 음성 인식 취소
     */
    fun cancelSpeechRecognition() {
        speechToTextUtils.cancelListening()
        _isListening.value = false
    }
    
    /**
     * 음성 에러 메시지 초기화
     */
    fun clearSpeechError() {
        _speechError.value = ""
    }
    
    /**
     * 카카오 로그인
     */
    fun loginKakao(context: Context) {
        KakaoUtils.loginKakao(
            context = context,
            onSuccess = { token ->
                _isKakaoLoggedIn.value = true
                val currentSessionId = _activeSessionId.value
                val repository = repositories[currentSessionId]
                
                // 로그인 성공 메시지 추가
                val successMessage = ChatMessage(
                    content = "카카오 로그인에 성공했습니다. 이제 메시지를 전송할 수 있습니다.",
                    isUser = false
                )
                repository?.addMessage(successMessage)
                
                if (currentSessionId != null) {
                    updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
                }
            },
            onFailure = { error ->
                val currentSessionId = _activeSessionId.value
                val repository = repositories[currentSessionId]
                
                val errorMessage = ChatMessage(
                    content = KakaoUtils.getErrorMessage(error),
                    isUser = false
                )
                repository?.addMessage(errorMessage)
                
                if (currentSessionId != null) {
                    updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
                }
            }
        )
    }
    
    /**
     * 카카오톡 메시지 전송 (공유 화면으로)
     */
    fun sendKakaoMessage(context: Context, messageText: String) {
        if (!_isKakaoLoggedIn.value) {
            // 로그인이 안 되어 있으면 로그인 유도
            loginKakao(context)
            return
        }
        
        if (messageText.isBlank()) {
            val currentSessionId = _activeSessionId.value
            val repository = repositories[currentSessionId]
            
            val errorMessage = ChatMessage(
                content = "전송할 메시지가 비어있습니다.",
                isUser = false
            )
            repository?.addMessage(errorMessage)
            
            if (currentSessionId != null) {
                updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
            }
            return
        }
        
        // 공유하기 실행
        shareKakaoMessage(context, messageText)
        _showKakaoDialog.value = false
    }
    
    /**
     * 카카오톡 메시지 전송 (이미지 포함)
     */
    fun sendKakaoMessageWithImage(context: Context, messageText: String, imageUri: String?) {
        if (!_isKakaoLoggedIn.value) {
            // 로그인이 안 되어 있으면 로그인 유도
            loginKakao(context)
            return
        }
        
        if (messageText.isBlank() && imageUri == null) {
            val currentSessionId = _activeSessionId.value
            val repository = repositories[currentSessionId]
            
            val errorMessage = ChatMessage(
                content = "전송할 메시지나 이미지 중 하나는 필요합니다.",
                isUser = false
            )
            repository?.addMessage(errorMessage)
            
            if (currentSessionId != null) {
                updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
            }
            return
        }
        
        // 공유하기 실행
        shareKakaoMessageWithImage(context, messageText, imageUri)
        _showKakaoDialog.value = false
        _kakaoImageUri.value = null  // 전송 후 이미지 초기화
    }
    
    /**
     * 카카오톡 이미지 제거
     */
    fun removeKakaoImage() {
        _kakaoImageUri.value = null
    }
    
    /**
     * 카카오톡 이미지 선택
     */
    fun selectKakaoImage(uri: Uri) {
        _kakaoImageUri.value = uri.toString()
    }
    
    /**
     * 카카오톡 다이얼로그 닫기
     */
    fun dismissKakaoDialog() {
        _showKakaoDialog.value = false
        _kakaoImageUri.value = null  // 다이얼로그 닫을 때 이미지 초기화
    }
    
    /**
     * 카카오톡 공유하기 (사용자가 채팅방 선택)
     */
    fun shareKakaoMessage(context: Context, message: String) {
        if (!_isKakaoLoggedIn.value) {
            loginKakao(context)
            return
        }
        
        viewModelScope.launch {
            val currentSessionId = _activeSessionId.value
            val repository = repositories[currentSessionId]
            
            KakaoUtils.shareMessage(
                context = context,
                text = message,
                onSuccess = {
                    val successMessage = ChatMessage(
                        content = "카카오톡 공유 화면이 열렸습니다. 전송할 대화방을 선택하세요.",
                        isUser = false
                    )
                    repository?.addMessage(successMessage)
                    
                    if (currentSessionId != null) {
                        updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
                    }
                },
                onFailure = { error ->
                    val errorMessage = ChatMessage(
                        content = KakaoUtils.getErrorMessage(error),
                        isUser = false
                    )
                    repository?.addMessage(errorMessage)
                    
                    if (currentSessionId != null) {
                        updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
                    }
                }
            )
        }
    }
    
    /**
     * 카카오톡 공유하기 (이미지 포함)
     */
    fun shareKakaoMessageWithImage(context: Context, message: String, imageUri: String?) {
        if (!_isKakaoLoggedIn.value) {
            loginKakao(context)
            return
        }
        
        viewModelScope.launch {
            val currentSessionId = _activeSessionId.value
            val repository = repositories[currentSessionId]
            
            if (imageUri != null) {
                // 이미지가 있는 경우 이미지 + 텍스트 전송
                KakaoUtils.shareImage(
                    context = context,
                    imageUri = Uri.parse(imageUri),
                    text = message, // 메시지도 함께 전송
                    onSuccess = {
                        val successMessage = ChatMessage(
                            content = "카카오톡으로 이미지와 메시지 전송 화면이 열렸습니다.",
                            isUser = false
                        )
                        repository?.addMessage(successMessage)
                        
                        if (currentSessionId != null) {
                            updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
                        }
                    },
                    onFailure = { error ->
                        val errorMessage = ChatMessage(
                            content = "이미지 전송 실패: ${error.message}",
                            isUser = false
                        )
                        repository?.addMessage(errorMessage)
                        
                        if (currentSessionId != null) {
                            updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
                        }
                    }
                )
            } else {
                // 이미지가 없는 경우 일반 메시지 전송
                shareKakaoMessage(context, message)
            }
        }
    }
    
    /**
     * 친구 목록 불러오기
     */
    fun loadFriendsList() {
        if (!_isKakaoLoggedIn.value) {
            val currentSessionId = _activeSessionId.value
            val repository = repositories[currentSessionId]
            
            val errorMessage = ChatMessage(
                content = "친구 목록을 불러오려면 카카오 로그인이 필요합니다.",
                isUser = false
            )
            repository?.addMessage(errorMessage)
            
            if (currentSessionId != null) {
                updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
            }
            return
        }
        
        _isFriendsLoading.value = true
        
        KakaoUtils.getFriendsList(
            onSuccess = { friends ->
                _friendsList.value = friends
                _isFriendsLoading.value = false
                _showFriendPicker.value = true
                
                println("DEBUG: Loaded ${friends.size} friends")
            },
            onFailure = { error ->
                _isFriendsLoading.value = false
                
                val currentSessionId = _activeSessionId.value
                val repository = repositories[currentSessionId]
                
                val errorMessage = ChatMessage(
                    content = "친구 목록을 불러오는데 실패했습니다. 카카오톡 친구 목록 동의를 확인해주세요.\n\n" +
                            "Kakao Developers에서 '카카오 서비스 내 친구 목록' 권한을 활성화해야 합니다.",
                    isUser = false
                )
                repository?.addMessage(errorMessage)
                
                if (currentSessionId != null) {
                    updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
                }
            }
        )
    }
    
    /**
     * 친구 선택 후 메시지 전송
     */
    fun sendMessageToFriend(context: Context, friend: Friend) {
        val messageText = _pendingFriendMessage.value
        
        if (messageText.isBlank()) {
            val currentSessionId = _activeSessionId.value
            val repository = repositories[currentSessionId]
            
            val errorMessage = ChatMessage(
                content = "전송할 메시지가 비어있습니다.",
                isUser = false
            )
            repository?.addMessage(errorMessage)
            
            if (currentSessionId != null) {
                updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
            }
            return
        }
        
        viewModelScope.launch {
            KakaoUtils.sendTextMessageToFriends(
                text = messageText,
                receiverUuids = listOf(friend.uuid),
                onSuccess = {
                    val currentSessionId = _activeSessionId.value
                    val repository = repositories[currentSessionId]
                    
                    val successMessage = ChatMessage(
                        content = "${friend.nickname}님에게 메시지를 전송했습니다: \"$messageText\"",
                        isUser = false
                    )
                    repository?.addMessage(successMessage)
                    
                    if (currentSessionId != null) {
                        updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
                    }
                    
                    _showFriendPicker.value = false
                    _pendingFriendMessage.value = ""
                    _targetFriendName.value = null
                },
                onFailure = { error ->
                    val currentSessionId = _activeSessionId.value
                    val repository = repositories[currentSessionId]
                    
                    val errorMessage = ChatMessage(
                        content = KakaoUtils.getErrorMessage(error),
                        isUser = false
                    )
                    repository?.addMessage(errorMessage)
                    
                    if (currentSessionId != null) {
                        updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
                    }
                    
                    _showFriendPicker.value = false
                }
            )
        }
    }
    
    /**
     * 친구 선택 다이얼로그 닫기
     */
    fun dismissFriendPicker() {
        _showFriendPicker.value = false
        _pendingFriendMessage.value = ""
        _targetFriendName.value = null
    }
    
    /**
     * 이미지 선택 다이얼로그 표시
     */
    fun showImagePicker() {
        _showImagePicker.value = true
    }
    
    /**
     * 이미지 선택 다이얼로그 닫기 (취소 버튼)
     */
    fun dismissImagePicker() {
        _showImagePicker.value = false
        _isPendingImageShare.value = false  // 취소 시 플래그 리셋
        _pendingShareMessage.value = ""  // 메시지도 초기화
    }
    
    /**
     * 이미지 선택 다이얼로그 숨기기 (이미지 선택 시 - 플래그 유지)
     */
    private fun hideImagePicker() {
        _showImagePicker.value = false
        // _isPendingImageShare와 _pendingShareMessage는 유지!
    }
    
    /**
     * 이미지 선택 완료 (채팅 입력창에 첨부)
     */
    fun onImageSelected(context: Context, uri: Uri) {
        println("🔵🔵🔵 onImageSelected called")
        println("🔵 _isPendingImageShare: ${_isPendingImageShare.value}")
        println("🔵 _pendingShareMessage: [${_pendingShareMessage.value}]")
        
        _showImagePicker.value = false
        
        // 이미지 전송 의도로 선택한 경우 저장된 메시지와 함께 다시 전송
        if (_isPendingImageShare.value) {
            val originalMessage = _pendingShareMessage.value
            println("=" .repeat(70))
            println("🔵 onImageSelected: Image selected for share intent")
            println("🔵 Original message: [$originalMessage]")
            println("🔵 Image URI: [$uri]")
            println("=" .repeat(70))
            
            _isPendingImageShare.value = false
            _pendingShareMessage.value = ""
            
            // 원래 메시지와 이미지를 함께 전송 (의도 재판별)
            val currentSessionId = _activeSessionId.value
            val repository = repositories[currentSessionId]
            
            if (currentSessionId != null && repository != null) {
                viewModelScope.launch {
                    println("🔵 Calling repository.sendMessage with message and image")
                    val result = repository.sendMessage(
                        message = originalMessage,
                        apiKey = _apiKey.value,
                        vertexApiKey = _vertexApiKey.value,
                        context = context,
                        imageUri = uri.toString()
                    )
                    
                    result.onSuccess { response ->
                        // 응답 처리
                        responseHandler.handleResponse(
                            response = response,
                            context = context,
                            repository = repository,
                            sessionId = currentSessionId,
                            onSessionUpdate = ::updateSessionMessages,
                            onShowMapDialog = { url -> 
                                _mapUrl.value = url
                                _showMapDialog.value = true
                            },
                            onShowImagePicker = {
                                _isPendingImageShare.value = true
                                _showImagePicker.value = true
                            }
                        )
                    }
                    
                    result.onFailure { error ->
                        repository.addMessage(ChatMessage(
                            content = "오류: ${error.message}",
                            isUser = false
                        ))
                        updateSessionMessages(currentSessionId, repository.messages.value)
                    }
                }
            }
        } else {
            // 일반 이미지 첨부 (채팅 입력창에 첨부)
            _selectedImageUri.value = uri.toString()
        }
    }
    
    /**
     * 선택된 이미지 제거
     */
    fun removeSelectedImage() {
        _selectedImageUri.value = null
    }
    
    /**
     * 이미지 메시지 전송
     */
    private fun sendImageMessage(imagePath: String) {
        if (!_isKakaoLoggedIn.value) {
            val currentSessionId = _activeSessionId.value
            val repository = repositories[currentSessionId]
            
            val errorMessage = ChatMessage(
                content = "이미지를 전송하려면 카카오 로그인이 필요합니다.",
                isUser = false
            )
            repository?.addMessage(errorMessage)
            
            if (currentSessionId != null) {
                updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
            }
            return
        }
        
        viewModelScope.launch {
            val currentSessionId = _activeSessionId.value
            val repository = repositories[currentSessionId]
            val context = getApplication<Application>()
            
            // 업로드 중 메시지
            val uploadingMessage = ChatMessage(
                content = "이미지를 업로드하고 있습니다...",
                isUser = false
            )
            repository?.addMessage(uploadingMessage)
            
            if (currentSessionId != null) {
                updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
            }
            
            KakaoUtils.uploadAndSendImage(
                context = context,
                imagePath = imagePath,
                onSuccess = { imageUrl ->
                    val successMessage = ChatMessage(
                        content = "카카오톡 공유 화면이 열렸습니다. 전송할 대화방을 선택하세요.\n이미지 URL: $imageUrl",
                        isUser = false
                    )
                    repository?.addMessage(successMessage)
                    
                    if (currentSessionId != null) {
                        updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
                    }
                    
                    // 임시 파일 삭제
                    FileUtils.deleteTempFile(File(imagePath))
                },
                onFailure = { error ->
                    val errorMessage = ChatMessage(
                        content = "이미지 전송에 실패했습니다: ${error.message}",
                        isUser = false
                    )
                    repository?.addMessage(errorMessage)
                    
                    if (currentSessionId != null) {
                        updateSessionMessages(currentSessionId, repository?.messages?.value ?: emptyList())
                    }
                    
                    // 임시 파일 삭제
                    FileUtils.deleteTempFile(File(imagePath))
                }
            )
        }
    }
    
    
    /**
     * 설정 다이얼로그 표시
     */
    fun showSettings() {
        _showSettings.value = true
    }
    
    /**
     * 설정 다이얼로그 닫기
     */
    fun dismissSettings() {
        _showSettings.value = false
    }
    
    fun showApiKeyDialog() {
        _showApiKeyDialog.value = true
    }
    
    fun dismissApiKeyDialog() {
        _showApiKeyDialog.value = false
    }
    
    fun toggleMoreMenu() {
        _showMoreMenu.value = !_showMoreMenu.value
    }
    
    fun dismissMoreMenu() {
        _showMoreMenu.value = false
    }
    
    /**
     * 글자 크기 변경
     */
    fun updateFontSizeScale(scale: Float) {
        _fontSizeScale.value = scale
        preferencesManager.saveFontSizeScale(scale)
    }
    
    /**
     * 고대비 모드 변경
     */
    fun updateHighContrastMode(enabled: Boolean) {
        _highContrastMode.value = enabled
        preferencesManager.saveHighContrastMode(enabled)
    }
    
    /**
     * Accent Color Preset 변경
     */
    fun updateAccentColorPreset(preset: Int) {
        _accentColorPreset.value = preset
        preferencesManager.saveAccentColorPreset(preset)
    }
    
    override fun onCleared() {
        super.onCleared()
        speechToTextUtils.destroy()
    }
}
