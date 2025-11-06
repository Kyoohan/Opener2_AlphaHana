package com.kyoohan.opener2.utils

/**
 * Repository의 응답 타입을 정의
 */
sealed class ChatResponse {
    data class Text(val content: String) : ChatResponse()
    data class NavigationLink(val deepLink: String) : ChatResponse()
    data class ImagePickerRequest(val message: String = "") : ChatResponse()
    data class KakaoSdkImageShare(val imageUri: String) : ChatResponse()
    data class AndroidImageShare(val imageUri: String) : ChatResponse()
    data class KakaoMessageShare(val message: String) : ChatResponse()
    data class PlayStoreLink(val packageName: String, val appName: String) : ChatResponse()
    data class Error(val message: String) : ChatResponse()
}

/**
 * ChatResponse를 문자열로 인코딩/디코딩
 */
object ResponseCodec {
    
    fun encode(response: ChatResponse): String {
        return when (response) {
            is ChatResponse.Text -> response.content
            is ChatResponse.NavigationLink -> response.deepLink
            is ChatResponse.ImagePickerRequest -> "IMAGE_SEND:${response.message}"
            is ChatResponse.KakaoSdkImageShare -> "KAKAO_SDK_IMAGE_SHARE:${response.imageUri}"
            is ChatResponse.AndroidImageShare -> "ANDROID_IMAGE_SHARE:${response.imageUri}"
            is ChatResponse.KakaoMessageShare -> "KAKAO_MESSAGE_SHARE:${response.message}"
            is ChatResponse.PlayStoreLink -> "PLAYSTORE_LINK:${response.packageName}:${response.appName}"
            is ChatResponse.Error -> response.message
        }
    }
    
    fun decode(encoded: String): ChatResponse {
        return when {
            encoded.startsWith("IMAGE_SEND:", ignoreCase = true) -> {
                val message = encoded.removePrefix("IMAGE_SEND:").trim()
                ChatResponse.ImagePickerRequest(message)
            }
            encoded.startsWith("KAKAO_SDK_IMAGE_SHARE:", ignoreCase = true) -> {
                val imageUri = encoded.removePrefix("KAKAO_SDK_IMAGE_SHARE:").trim()
                ChatResponse.KakaoSdkImageShare(imageUri)
            }
            encoded.startsWith("ANDROID_IMAGE_SHARE:", ignoreCase = true) -> {
                val imageUri = encoded.removePrefix("ANDROID_IMAGE_SHARE:").trim()
                ChatResponse.AndroidImageShare(imageUri)
            }
            encoded.startsWith("KAKAO_MESSAGE_SHARE:", ignoreCase = true) -> {
                val message = encoded.removePrefix("KAKAO_MESSAGE_SHARE:").trim()
                ChatResponse.KakaoMessageShare(message)
            }
            encoded.startsWith("PLAYSTORE_LINK:", ignoreCase = true) -> {
                val content = encoded.removePrefix("PLAYSTORE_LINK:").trim()
                val parts = content.split(":", limit = 2)
                if (parts.size == 2) {
                    ChatResponse.PlayStoreLink(parts[0], parts[1])
                } else {
                    ChatResponse.Error("잘못된 PlayStore 링크 형식입니다.")
                }
            }
            encoded.startsWith("nmap://", ignoreCase = true) -> {
                ChatResponse.NavigationLink(encoded)
            }
            else -> ChatResponse.Text(encoded)
        }
    }
}


