package com.kyoohan.opener2.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.kyoohan.opener2.data.*
import com.kyoohan.opener2.network.GeminiApiService
import com.kyoohan.opener2.network.NetworkModule
import com.kyoohan.opener2.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream

class ChatRepository {
    private val apiService: GeminiApiService = NetworkModule.geminiApiService
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    /**
     * 메시지 전송 - 간소화된 버전
     */
    suspend fun sendMessage(
        message: String, 
        apiKey: String, 
        vertexApiKey: String? = null,
        context: Context? = null,
        imageUri: String? = null
    ): Result<String> {
        if (message.isBlank() && imageUri == null) {
            return Result.failure(Exception("메시지나 이미지 중 하나는 필요합니다"))
        }
        if (apiKey.isBlank()) return Result.failure(Exception("API 키가 설정되지 않았습니다"))
        
        // 사용자 메시지 추가
        val userMessage = ChatMessage(content = message, isUser = true, imageUri = imageUri)
        _messages.value = _messages.value + userMessage
        _isLoading.value = true
        
        return try {
            // 이미지가 첨부된 경우
            if (imageUri != null && context != null) {
                handleImageMessage(message, imageUri, context, apiKey)
            }
            // 이미지 없이 메시지만 있는 경우
            else {
                handleTextMessage(message, apiKey, vertexApiKey)
            }
        } catch (e: Exception) {
            println("ERROR: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * 이미지가 포함된 메시지 처리
     */
    private suspend fun handleImageMessage(
        message: String,
        imageUri: String,
        context: Context,
        apiKey: String
    ): Result<String> {
        println("🟢 Repository.handleImageMessage called")
        println("🟢 Message: [$message]")
        println("🟢 ImageUri: [$imageUri]")
        
        val intent = IntentDetector.detectImageShareIntent(message)
        println("🟢 Detected intent: $intent")
        
        return when (intent) {
            IntentDetector.ImageShareIntent.KakaoSdk -> {
                println("🟢 ✅ Kakao SDK image share")
                val response = ResponseCodec.encode(ChatResponse.KakaoSdkImageShare(imageUri))
                println("🟢 Response: $response")
                Result.success(response)
            }
            IntentDetector.ImageShareIntent.AndroidShare -> {
                println("🟢 ✅ Android image share")
                val response = ResponseCodec.encode(ChatResponse.AndroidImageShare(imageUri))
                println("🟢 Response: $response")
                Result.success(response)
            }
            IntentDetector.ImageShareIntent.ImageAnalysis -> {
                println("🟢 ✅ Image analysis with Gemini")
                analyzeImageWithGemini(message, imageUri, context, apiKey)
            }
            IntentDetector.ImageShareIntent.None -> {
                // None이 반환되면 기본적으로 이미지 분석 수행
                println("🟢 ✅ Image analysis with Gemini (None case)")
                analyzeImageWithGemini(message, imageUri, context, apiKey)
            }
        }
    }
    
    /**
     * 텍스트 메시지 처리
     */
    private suspend fun handleTextMessage(
        message: String,
        apiKey: String,
        vertexApiKey: String?
    ): Result<String> {
        // 이미지 선택 요청 체크
        if (IntentDetector.hasImageRequestKeywords(message)) {
            println("✅ Image request detected")
            return Result.success(ResponseCodec.encode(ChatResponse.ImagePickerRequest()))
        }
        
        // 카카오톡 메시지 전송 체크
        if (IntentDetector.hasKakaoMessageKeywords(message)) {
            println("✅ Kakao message send detected")
            
            // 사진/이미지 키워드가 있으면 이미지 공유만, 없으면 텍스트 메시지만
            val isImageOnly = IntentDetector.hasKakaoImageKeywords(message)
            
            if (isImageOnly) {
                // 이미지만 전송 (IMAGE_SEND 프롬프트 사용)
                return Result.success(ResponseCodec.encode(ChatResponse.ImagePickerRequest("카카오톡으로 사진을 보내주세요.")))
            } else {
                // 텍스트 메시지만 전송
                val extractedMessage = extractKakaoMessage(message)
                return Result.success(ResponseCodec.encode(ChatResponse.KakaoMessageShare(extractedMessage)))
            }
        }
        
        // 앱 설치 요청 체크
        if (IntentDetector.hasAppInstallKeywords(message)) {
            println("✅ App install request detected")
            val appName = IntentDetector.extractAppName(message)
            if (appName != null && AppPackageDatabase.isAppSupported(appName)) {
                val packageName = AppPackageDatabase.getPackageName(appName)
                if (packageName != null) {
                    return Result.success(ResponseCodec.encode(ChatResponse.PlayStoreLink(packageName, appName)))
                }
            }
            // 지원하지 않는 앱인 경우 LLM으로 넘어가서 적절한 답변 제공
            println("⚠️ Unsupported app requested, falling back to LLM")
        }
        
        // 길찾기 체크
        if (IntentDetector.hasNavigationKeywords(message)) {
            println("✅ Navigation request detected")
            return try {
                val deepLink = getMapUrlOrThrow(message)
                Result.success(ResponseCodec.encode(ChatResponse.NavigationLink(deepLink)))
            } catch (e: Exception) {
                // 길찾기 실패 시 일반 대화로 fallback
                println("⚠️ Navigation failed, fallback to chat: ${e.message}")
                chatWithGemini(message, apiKey)
            }
        }
        
        // 일반 대화
        return chatWithGemini(message, apiKey)
    }
    
    /**
     * Gemini로 이미지 분석
     */
    private suspend fun analyzeImageWithGemini(
        text: String,
        imageUri: String,
        context: Context,
        apiKey: String
    ): Result<String> {
        val base64Image = encodeImageToBase64(context, imageUri)
        val mimeType = getMimeType(context, imageUri)
        
        if (base64Image == null || mimeType == null) {
            return Result.failure(Exception("이미지를 읽을 수 없습니다"))
        }
        
        val parts = mutableListOf<Part>()
        if (text.isNotBlank()) {
            parts.add(Part(text = text))
        }
        parts.add(Part(inlineData = InlineData(mimeType = mimeType, data = base64Image)))
        
        val request = GeminiRequest(
            contents = buildConversationHistory() + Content(parts = parts, role = "user"),
            systemInstruction = SystemInstruction(
                parts = listOf(Part(text = "이미지를 분석하고 간결하게 답변하세요 (6문장 이내)."))
            )
        )
        
        val response = apiService.generateContent(apiKey, request)
        if (response.isSuccessful) {
            val aiResponse = response.body()?.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text
                ?: "이미지를 분석할 수 없습니다."
            
            addAIMessage(aiResponse)
            return Result.success(ResponseCodec.encode(ChatResponse.Text(aiResponse)))
        }
        
        // 에러 응답 본문 파싱
        val errorBody = response.errorBody()?.string()
        val errorMessage = if (errorBody != null) {
            try {
                val gson = com.google.gson.Gson()
                val error = gson.fromJson(errorBody, GeminiError::class.java)
                error.error.message
            } catch (e: Exception) {
                "API 요청 실패: ${response.code()}"
            }
        } else {
            "API 요청 실패: ${response.code()}"
        }
        
        println("ERROR: API 요청 실패 - Code: ${response.code()}, Message: $errorMessage")
        return Result.failure(Exception(errorMessage))
    }
    
    /**
     * Gemini와 일반 대화
     */
    private suspend fun chatWithGemini(message: String, apiKey: String): Result<String> {
        // 앱 설치 요청인지 확인
        val isAppInstallRequest = IntentDetector.hasAppInstallKeywords(message)
        val appName = if (isAppInstallRequest) IntentDetector.extractAppName(message) else null
        
        val systemInstruction = if (isAppInstallRequest && appName == null) {
            // 지원하지 않는 앱 설치 요청인 경우
            SystemInstruction(
                parts = listOf(Part(text = """
                    사용자가 앱 설치를 요청했지만 현재 지원하지 않는 앱입니다.
                    다음과 같이 답변하세요:
                    "죄송합니다. 해당 앱은 현재 지원하지 않습니다. 
                    지원하는 앱: ${AppPackageDatabase.getSupportedApps().joinToString(", ")}
                    다른 앱을 요청해주시거나, 직접 플레이스토어에서 검색해보세요."
                """.trimIndent()))
            )
        } else {
            // 일반 대화 (RAG 활성화)
            SystemInstruction(
                parts = listOf(Part(text = """
                    답변 작성 규칙:
                    1. 일반적인 질문은 6문장 이내로 간결하게 답변하세요
                    2. 순위/목록/비교표는 완전하게 제공하세요
                    3. 실시간 정보, 최신 뉴스, 날씨, 주가, 이벤트 등이 필요하면 반드시 Google 검색을 활용하세요
                    4. 검색 결과를 바탕으로 정확하고 최신 정보를 제공하세요
                    5. 불필요한 인사말은 생략하세요
                """.trimIndent()))
            )
        }
        
        val request = GeminiRequest(
            contents = buildConversationHistory() + Content(
                parts = listOf(Part(text = message)),
                role = "user"
            ),
            systemInstruction = systemInstruction,
            generationConfig = GenerationConfig(temperature = 0.7f, topP = 0.95f, topK = 40),
            tools = listOf(Tool(googleSearch = GoogleSearch()))
        )
        
        val response = apiService.generateContent(apiKey, request)
        if (response.isSuccessful) {
            val candidate = response.body()?.candidates?.firstOrNull()
            val aiResponse = candidate?.content?.parts?.firstOrNull()?.text
                ?: "응답을 받을 수 없습니다."
            
            // RAG: Grounding metadata에서 검색 결과 추출 및 포함
            val groundingMetadata = candidate?.groundingMetadata
            val enhancedResponse = if (groundingMetadata != null) {
                buildResponseWithSources(aiResponse, groundingMetadata)
            } else {
                aiResponse
            }
            
            addAIMessage(enhancedResponse)
            return Result.success(ResponseCodec.encode(ChatResponse.Text(enhancedResponse)))
        }
        
        // 에러 응답 본문 파싱
        val errorBody = response.errorBody()?.string()
        val errorMessage = if (errorBody != null) {
            try {
                val gson = com.google.gson.Gson()
                val error = gson.fromJson(errorBody, GeminiError::class.java)
                error.error.message
            } catch (e: Exception) {
                "API 요청 실패: ${response.code()}"
            }
        } else {
            "API 요청 실패: ${response.code()}"
        }
        
        println("ERROR: API 요청 실패 - Code: ${response.code()}, Message: $errorMessage")
        println("ERROR: 사용된 API 키: ${apiKey.take(10)}...")
        return Result.failure(Exception(errorMessage))
    }
    
    /**
     * RAG: Grounding metadata를 사용하여 응답에 출처 정보 추가
     */
    private fun buildResponseWithSources(
        response: String,
        groundingMetadata: GroundingMetadata
    ): String {
        val sources = mutableListOf<String>()
        
        // 검색 쿼리 로그
        groundingMetadata.webSearchQueries?.forEach { query ->
            println("🔍 RAG 검색 쿼리: $query")
        }
        
        // 검색 결과에서 출처 추출
        groundingMetadata.groundingChunks?.forEachIndexed { index, chunk ->
            chunk.web?.let { web ->
                val title = web.title ?: "출처 ${index + 1}"
                val uri = web.uri ?: ""
                if (uri.isNotEmpty()) {
                    sources.add("[$title]($uri)")
                }
            }
        }
        
        // 출처가 있으면 응답에 추가
        return if (sources.isNotEmpty()) {
            val sourcesText = "\n\n**참고 출처:**\n${sources.joinToString("\n")}"
            response + sourcesText
        } else {
            response
        }
    }
    
    /**
     * 대화 히스토리 생성 (최근 10개 메시지)
     */
    private fun buildConversationHistory(): List<Content> {
        return _messages.value.takeLast(10).map { msg ->
            Content(
                parts = listOf(Part(text = msg.content)),
                role = if (msg.isUser) "user" else "model"
            )
        }
    }
    
    /**
     * 카카오톡 메시지 내용 추출
     */
    private fun extractKakaoMessage(message: String): String {
        // "OO라고 카톡 보내줘" → "OO" 형태로 추출
        val patterns = listOf(
            // "~라고(고/랑)" 패턴 제거
            Regex("(.+?)(?:라고|라고 |라고해서|고 |랑 )(?:카카오톡|카톡)", RegexOption.IGNORE_CASE),
            Regex("카카오톡으로\\s+[\"']?(.+?)[\"']?\\s*보내", RegexOption.IGNORE_CASE),
            Regex("카톡으로\\s+[\"']?(.+?)[\"']?\\s*보내", RegexOption.IGNORE_CASE),
            Regex("메시지\\s+[\"']?(.+?)[\"']?\\s*보내", RegexOption.IGNORE_CASE),
            Regex("[\"'](.+?)[\"']\\s*(?:를|을)?\\s*카카오톡", RegexOption.IGNORE_CASE)
        )
        
        for (pattern in patterns) {
            val matchResult = pattern.find(message)
            if (matchResult != null && matchResult.groupValues.size > 1) {
                var extracted = matchResult.groupValues[1].trim()
                // "~라고" 등 추가 키워드 제거
                extracted = extracted.replace(Regex("(?:라고|라고 |라고해서|고 |랑 |라고 말해|라고 말하면|이라고)[\\s]*$"), "").trim()
                if (extracted.isNotEmpty()) {
                    return extracted
                }
            }
        }
        
        // 패턴이 매치되지 않으면 원본 메시지에서 키워드 제거
        return message
            .replace(Regex("카카오톡으로|카톡으로|메시지로|보내줘|전송해줘|보내|전송|라고|라고 |라고해서|고 |랑 |라고 말해|라고 말하면|이라고"), "")
            .trim()
            .removePrefix("\"")
            .removeSuffix("\"")
            .removePrefix("'")
            .removeSuffix("'")
    }
    
    /**
     * URI로부터 이미지를 Base64로 인코딩
     */
    private fun encodeImageToBase64(context: Context, uriString: String): String? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            bytes?.let { Base64.encodeToString(it, Base64.NO_WRAP) }
        } catch (e: Exception) {
            println("ERROR: Failed to encode image: ${e.message}")
            null
        }
    }
    
    /**
     * URI로부터 MIME 타입 가져오기
     */
    private fun getMimeType(context: Context, uriString: String): String? {
        return try {
            val uri = Uri.parse(uriString)
            context.contentResolver.getType(uri) ?: "image/jpeg"
        } catch (e: Exception) {
            "image/jpeg"
        }
    }
    
    private fun addAIMessage(content: String) {
        _messages.value = _messages.value + ChatMessage(content = content, isUser = false)
    }
    
    fun clearMessages() {
        _messages.value = emptyList()
    }
    
    fun addMessage(message: ChatMessage) {
        _messages.value = _messages.value + message
    }
}
