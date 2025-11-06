package com.kyoohan.opener2.utils

object IntentDetector {
    
    enum class ImageShareIntent {
        KakaoSdk,
        AndroidShare,
        ImageAnalysis,
        None
    }
    
    fun detectImageShareIntent(message: String): ImageShareIntent {
        // 메시지가 비어있으면 이미지 분석이 기본 (사진만 첨부한 경우)
        if (message.isBlank()) {
            return ImageShareIntent.ImageAnalysis
        }
        
        val lowerMessage = message.lowercase()
        
        return when {
            // 분석 의도가 명확한 경우 (우선순위가 가장 높음)
            lowerMessage.contains("분석") || lowerMessage.contains("뭐야") || lowerMessage.contains("무엇") || 
            lowerMessage.contains("설명") || lowerMessage.contains("알려") || lowerMessage.contains("이게") ||
            lowerMessage.contains("무슨") || lowerMessage.contains("뭔지") -> {
                ImageShareIntent.ImageAnalysis
            }
            // 카카오톡 공유 의도
            lowerMessage.contains("카톡") || lowerMessage.contains("카카오") || lowerMessage.contains("카카오톡") -> {
                ImageShareIntent.KakaoSdk
            }
            // 일반 공유 의도
            lowerMessage.contains("공유") || lowerMessage.contains("보내") || lowerMessage.contains("전송") -> {
                ImageShareIntent.AndroidShare
            }
            else -> {
                // 의도가 불분명한 경우에도 기본적으로 이미지 분석 (사진만 첨부한 경우를 고려)
                ImageShareIntent.ImageAnalysis
            }
        }
    }
    
    /**
     * 앱 설치 요청 키워드 감지
     */
    fun hasAppInstallKeywords(message: String): Boolean {
        val lowerMessage = message.lowercase()
        return lowerMessage.contains("설치") || lowerMessage.contains("다운") || 
               lowerMessage.contains("받아") || lowerMessage.contains("깔아") ||
               lowerMessage.contains("인스톨") || lowerMessage.contains("install")
    }
    
    /**
     * 메시지에서 앱 이름 추출
     */
    fun extractAppName(message: String): String? {
        // 공백 제거하고 소문자로 변환
        val lowerMessage = message.lowercase().replace(" ", "")
        
        // 인기 앱들의 매핑
        val appMappings = mapOf(
            "네이버" to "네이버",
            "naver" to "네이버",
            "카카오톡" to "카카오톡",
            "카톡" to "카카오톡",
            "kakao" to "카카오톡",
            "유튜브" to "유튜브",
            "youtube" to "유튜브",
            "인스타그램" to "인스타그램",
            "instagram" to "인스타그램",
            "인스타" to "인스타그램",
            "틱톡" to "틱톡",
            "tiktok" to "틱톡",
            "페이스북" to "페이스북",
            "facebook" to "페이스북",
            "왓츠앱" to "왓츠앱",
            "whatsapp" to "왓츠앱",
            "텔레그램" to "텔레그램",
            "telegram" to "텔레그램",
            "닥터나우" to "닥터나우",
            "닥터나" to "닥터나우",
            "doctornow" to "닥터나우"
        )
        
        for ((keyword, appName) in appMappings) {
            if (lowerMessage.contains(keyword)) {
                return appName
            }
        }
        
        // 앱 이름을 직접 매칭 시도 (AppPackageDatabase의 키워드와 매칭)
        val supportedApps = AppPackageDatabase.getSupportedApps()
        for (app in supportedApps) {
            // 공백 제거한 버전으로도 검색
            val appWithoutSpace = app.lowercase().replace(" ", "")
            if (lowerMessage.contains(app.lowercase()) || lowerMessage.contains(appWithoutSpace)) {
                return app
            }
        }
        
        return null
    }
    
    /**
     * 이미지 선택 요청 키워드 감지
     */
    fun hasImageRequestKeywords(message: String): Boolean {
        val lowerMessage = message.lowercase()
        return lowerMessage.contains("이미지") || lowerMessage.contains("사진") || 
               lowerMessage.contains("그림") || lowerMessage.contains("첨부") ||
               lowerMessage.contains("업로드") || lowerMessage.contains("선택")
    }
    
    /**
     * 카카오톡 메시지 전송 키워드 감지
     */
    fun hasKakaoMessageKeywords(message: String): Boolean {
        val lowerMessage = message.lowercase()
        return lowerMessage.contains("카톡") || lowerMessage.contains("카카오") || 
               lowerMessage.contains("카카오톡") || lowerMessage.contains("kakao")
    }
    
    /**
     * 카카오톡 이미지 전송 키워드 감지
     */
    fun hasKakaoImageKeywords(message: String): Boolean {
        val lowerMessage = message.lowercase()
        return lowerMessage.contains("사진") || lowerMessage.contains("이미지") || 
               lowerMessage.contains("그림") || lowerMessage.contains("photo") ||
               lowerMessage.contains("image") || lowerMessage.contains("picture")
    }
    
    /**
     * 길찾기/네비게이션 키워드 감지
     */
    fun hasNavigationKeywords(message: String): Boolean {
        val lowerMessage = message.lowercase()
        return lowerMessage.contains("길찾기") || lowerMessage.contains("길") || 
               lowerMessage.contains("가고") || lowerMessage.contains("가자") ||
               lowerMessage.contains("가려고") || lowerMessage.contains("가야") ||
               lowerMessage.contains("가고싶") || lowerMessage.contains("가고 싶") ||
               lowerMessage.contains("길") || lowerMessage.contains("경로") ||
               lowerMessage.contains("네비") || lowerMessage.contains("내비") ||
               lowerMessage.contains("navigation") || lowerMessage.contains("route")
    }
}
