package com.kyoohan.opener2.utils

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.TextTemplate
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.user.UserApiClient
import java.io.File

/**
 * 카카오톡 관련 유틸리티 함수
 */
object KakaoUtils {
    private const val TAG = "KakaoUtils"

    /**
     * 카카오 로그인 상태 확인
     */
    fun isKakaoLoggedIn(context: Context, callback: (Boolean) -> Unit) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    Log.e(TAG, "토큰 정보 조회 실패", error)
                    callback(false)
                } else {
                    callback(true)
                }
            }
        } else {
            callback(false)
        }
    }

    /**
     * 카카오 로그인 실행
     */
    fun loginKakao(
        context: Context,
        onSuccess: (OAuthToken) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오 로그인 실패", error)
                Log.e(TAG, "에러 메시지: ${error.message}")
                Log.e(TAG, "에러 타입: ${error.javaClass.simpleName}")
                error.printStackTrace()
                onFailure(error)
            } else if (token != null) {
                Log.i(TAG, "카카오 로그인 성공 ${token.accessToken}")
                onSuccess(token)
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    onSuccess(token)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    /**
     * 카카오 로그아웃
     */
    fun logoutKakao(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                onFailure(error)
            } else {
                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                onSuccess()
            }
        }
    }

    /**
     * 기본 텍스트 템플릿 메시지를 나에게 전송
     */
    fun sendTextMessageToMe(
        context: Context,
        text: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val defaultText = TextTemplate(
            text = text,
            link = Link(
                androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
            )
        )

        // 카카오톡 나에게 보내기 (Memo API 사용)
        TalkApiClient.instance.sendDefaultMemo(defaultText) { error ->
            if (error != null) {
                Log.e(TAG, "나에게 보내기 실패", error)
                Log.e(TAG, "에러 상세: ${error.message}")
                error.printStackTrace()
                onFailure(error)
            } else {
                Log.i(TAG, "나에게 보내기 성공")
                onSuccess()
            }
        }
    }

    /**
     * 친구에게 기본 텍스트 템플릿 메시지 전송
     * @param receiverUuids 친구 UUID 리스트 (최대 5명)
     */
    fun sendTextMessageToFriends(
        text: String,
        receiverUuids: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val defaultText = TextTemplate(
            text = text,
            link = Link(
                androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
            )
        )

        // 메시지 전송
        TalkApiClient.instance.sendDefaultMessage(receiverUuids, defaultText) { result, error ->
            if (error != null) {
                Log.e(TAG, "친구에게 메시지 전송 실패", error)
                onFailure(error)
            } else if (result != null) {
                Log.i(TAG, "친구에게 메시지 전송 성공: ${result.successfulReceiverUuids}")
                
                if (result.failureInfos != null) {
                    Log.e(TAG, "일부 친구에게 전송 실패: ${result.failureInfos}")
                }
                
                onSuccess()
            }
        }
    }
    
    /**
     * 카카오톡 공유하기 (사용자가 직접 채팅방 선택)
     * 친구 목록 권한 불필요
     */
    fun shareMessage(
        context: Context,
        text: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val defaultText = TextTemplate(
            text = text,
            link = Link(
                androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
            )
        )

        // 카카오톡 설치 확인
        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            // 카카오톡 공유하기 - 사용자가 채팅방 선택
            ShareClient.instance.shareDefault(context, defaultText) { sharingResult, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡 공유 실패", error)
                    Log.e(TAG, "에러 상세: ${error.message}")
                    onFailure(error)
                } else if (sharingResult != null) {
                    Log.i(TAG, "카카오톡 공유 성공")
                    Log.d(TAG, "Intent: ${sharingResult.intent}")
                    Log.d(TAG, "Argument: ${sharingResult.argumentMsg}")
                    
                    // 공유 화면 실행
                    context.startActivity(sharingResult.intent)
                    onSuccess()
                }
            }
        } else {
            // 카카오톡 미설치: 웹 공유
            val sharerUrl = WebSharerClient.instance.makeDefaultUrl(defaultText)
            try {
                ShareClient.instance.shareDefault(context, defaultText) { sharingResult, error ->
                    if (error != null) {
                        onFailure(error)
                    } else {
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "카카오톡이 설치되어 있지 않습니다.", e)
                onFailure(Exception("카카오톡이 설치되어 있지 않습니다."))
            }
        }
    }

    /**
     * 이미지 업로드 및 메시지 전송
     */
    fun uploadAndSendImage(
        context: Context,
        imagePath: String,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val imageFile = File(imagePath)
        
        if (!imageFile.exists()) {
            onFailure(Exception("이미지 파일을 찾을 수 없습니다: $imagePath"))
            return
        }

        // 1단계: 이미지 업로드
        ShareClient.instance.uploadImage(imageFile) { imageUploadResult, error ->
            if (error != null) {
                Log.e(TAG, "이미지 업로드 실패", error)
                onFailure(error)
            } else if (imageUploadResult != null) {
                val imageUrl = imageUploadResult.infos.original.url
                Log.i(TAG, "이미지 업로드 성공: $imageUrl")
                
                // 2단계: 피드 템플릿으로 메시지 전송
                val feedTemplate = FeedTemplate(
                    content = Content(
                        title = "이미지 전송",
                        imageUrl = imageUrl,
                        link = Link()
                    )
                )
                
                // 카카오톡 공유하기
                if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
                    ShareClient.instance.shareDefault(context, feedTemplate) { sharingResult, error2 ->
                        if (error2 != null) {
                            Log.e(TAG, "이미지 메시지 전송 실패", error2)
                            onFailure(error2)
                        } else if (sharingResult != null) {
                            Log.i(TAG, "이미지 메시지 전송 성공")
                            // 공유 화면 실행
                            context.startActivity(sharingResult.intent)
                            onSuccess(imageUrl)
                        }
                    }
                } else {
                    Log.e(TAG, "카카오톡이 설치되어 있지 않습니다.")
                    onFailure(Exception("카카오톡이 설치되어 있지 않습니다."))
                }
            }
        }
    }
    
    /**
     * 이미지를 카카오톡으로 공유 (안드로이드 표준 공유 인텐트 사용)
     * 카카오 로그인 불필요, Uri를 직접 사용 가능
     */
    fun shareImage(
        context: Context,
        imageUri: android.net.Uri,
        text: String? = null,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        try {
            val intent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                putExtra(android.content.Intent.EXTRA_STREAM, imageUri)
                // 텍스트가 있으면 함께 전송
                if (!text.isNullOrBlank()) {
                    putExtra(android.content.Intent.EXTRA_TEXT, text)
                }
                type = "image/*"
                // 카카오톡만 표시하도록 패키지 지정
                setPackage("com.kakao.talk")
            }
            
            // 카카오톡이 설치되어 있는지 확인
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Log.i(TAG, "카카오톡 이미지 공유 실행 (text: $text)")
                onSuccess()
            } else {
                // 카카오톡이 없으면 일반 공유 화면으로 폴백
                Log.w(TAG, "카카오톡이 설치되어 있지 않아 일반 공유 화면을 실행합니다")
                val fallbackIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_STREAM, imageUri)
                    if (!text.isNullOrBlank()) {
                        putExtra(android.content.Intent.EXTRA_TEXT, text)
                    }
                    type = "image/*"
                }
                
                val chooser = android.content.Intent.createChooser(fallbackIntent, "이미지 공유")
                context.startActivity(chooser)
                onSuccess()
            }
        } catch (e: Exception) {
            Log.e(TAG, "이미지 공유 실패", e)
            onFailure(e)
        }
    }

    /**
     * 카카오톡 친구 목록 조회
     */
    fun getFriendsList(
        onSuccess: (List<Friend>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        TalkApiClient.instance.friends { friends, error ->
            if (error != null) {
                Log.e(TAG, "친구 목록 조회 실패", error)
                onFailure(error)
            } else if (friends != null) {
                Log.i(TAG, "친구 목록 조회 성공\n${friends.elements?.joinToString("\n")}")
                
                val friendList = friends.elements?.map { friend ->
                    Friend(
                        uuid = friend.uuid,
                        nickname = friend.profileNickname ?: "Unknown",
                        thumbnailImage = friend.profileThumbnailImage
                    )
                } ?: emptyList()
                
                onSuccess(friendList)
            }
        }
    }

    /**
     * 에러 메시지 추출
     */
    fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("NotRegisteredUserException") == true -> 
                "카카오톡에 로그인이 필요합니다. 로그인 후 다시 시도해주세요."
            exception.message?.contains("NotSupportedException") == true ->
                "카카오톡이 설치되어 있지 않거나 지원되지 않는 기능입니다."
            exception.message?.contains("NetworkError") == true ->
                "네트워크 오류가 발생했습니다. 연결 상태를 확인해주세요."
            exception.message?.contains("InsufficientScope") == true ->
                "권한이 부족합니다. 카카오톡 메시지 전송 권한을 허용해주세요."
            else -> "메시지 전송에 실패했습니다: ${exception.message}"
        }
    }
}

/**
 * 친구 정보 데이터 클래스
 */
data class Friend(
    val uuid: String,
    val nickname: String,
    val thumbnailImage: String?
)

/**
 * 카카오톡 메시지 전송 의도 감지
 */
fun isKakaoMessageQuery(message: String): Boolean {
    val kakaoKeywords = listOf(
        "카카오톡", "카톡", "메시지", "메세지", "전송",
        "보내", "보내기", "문자", "채팅"
    )
    
    return kakaoKeywords.any { keyword ->
        message.contains(keyword, ignoreCase = true)
    }
}

/**
 * 메시지 내용 추출
 */
fun extractKakaoMessage(userInput: String): String {
    // "OO라고 카톡 보내줘" → "OO" 형태로 추출
    val patterns = listOf(
        // "~라고(고/랑)" 패턴 제거
        Regex("(.+?)(?:라고|라고 |라고해서|라고 |고 |랑 |라고 말해|라고 말하면|이라고)(?:카카오톡|카톡)", RegexOption.IGNORE_CASE),
        Regex("카카오톡으로\\s+[\"']?(.+?)[\"']?\\s*보내", RegexOption.IGNORE_CASE),
        Regex("카톡으로\\s+[\"']?(.+?)[\"']?\\s*보내", RegexOption.IGNORE_CASE),
        Regex("메시지\\s+[\"']?(.+?)[\"']?\\s*보내", RegexOption.IGNORE_CASE),
        Regex("[\"'](.+?)[\"']\\s*(?:를|을)?\\s*카카오톡", RegexOption.IGNORE_CASE),
        Regex("보내줘\\s+[\"']?(.+?)[\"']?", RegexOption.IGNORE_CASE)
    )
    
    for (pattern in patterns) {
        val matchResult = pattern.find(userInput)
        if (matchResult != null && matchResult.groupValues.size > 1) {
            var extracted = matchResult.groupValues[1].trim()
            // "~라고" 등 추가 키워드 제거
            extracted = extracted.replace(Regex("(?:라고|라고 |라고해서|라고 |고 |랑 |라고 말해|라고 말하면|이라고)[\\s]*$"), "").trim()
            if (extracted.isNotEmpty()) {
                return extracted
            }
        }
    }
    
    // 패턴이 매치되지 않으면 원본 메시지에서 키워드 제거
    return userInput
        .replace(Regex("카카오톡으로|카톡으로|메시지로|보내줘|전송해줘|보내|전송|라고|라고 |라고해서|라고 |고 |랑 |라고 말해|라고 말하면|이라고"), "")
        .trim()
        .removePrefix("\"")
        .removeSuffix("\"")
        .removePrefix("'")
        .removeSuffix("'")
}

