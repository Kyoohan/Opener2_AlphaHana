package com.kyoohan.opener2.utils

/**
 * 인기 앱들의 패키지명 데이터베이스
 */
object AppPackageDatabase {
    
    /**
     * 앱 이름과 패키지명 매핑
     */
    private val appPackages = mapOf(
        // 포털/커뮤니케이션
        "네이버" to "com.nhn.android.search",
        "카카오톡" to "com.kakao.talk",
        "다음" to "net.daum.android.daum",
        "유튜브" to "com.google.android.youtube",
        "인스타그램" to "com.instagram.android",
        "트위터" to "com.twitter.android",
        "디스코드" to "com.discord",
        "당근마켓" to "com.towneers.www",

        // 미디어/쇼핑
        "쿠팡" to "com.coupang.mobile",
        "아마존" to "com.amazon.mShop.android.shopping",
        "넷플릭스" to "com.netflix.mediaclient",
        "스포티파이" to "com.spotify.music",
        "GS SHOP" to "gsshop.mobile.v2",
        "현대홈쇼핑" to "com.hmallapp",

        // 배달
        "배달의민족" to "com.baemin.app",
        "요기요" to "com.fineapp.yogiyo",

        // 이동/교통
        "티맵" to "com.skt.tmap.ku",
        "구글맵" to "com.google.android.apps.maps",
        "네이버맵" to "com.nhn.android.nmap",
        "카카오맵" to "net.daum.android.map",

        // 번역/클라우드
        "구글번역" to "com.google.android.apps.translate",
        "파파고" to "com.naver.labs.translator",
        "구글드라이브" to "com.google.android.apps.docs",
        "원드라이브" to "com.microsoft.skydrive",
        "드롭박스" to "com.dropbox.android",
        "구글포토" to "com.google.android.apps.photos",

        // 일정/메모
        "구글캘린더" to "com.google.android.calendar",
        "삼성캘린더" to "com.samsung.android.calendar",
        "구글메모" to "com.google.android.keep",
        "삼성메모" to "com.samsung.android.app.notes",

        // 건강/의료
        "닥터나우" to "com.baedalyakgook_user",
        "The건강보험" to "kr.or.nhic",
        "모바일건강보험증" to "kr.or.nhiq",
        "삼성헬스" to "com.sec.android.app.shealth",

        // 기타
        "줌" to "us.zoom.videomeetings",
        "구글미트" to "com.google.android.apps.tachyon",
        "폴라리스오피스" to "com.infraware.office.link",
        "정부24" to "kr.go.minwon.m"    
    )
    
    /**
     * 앱 이름으로 패키지명 조회
     */
    fun getPackageName(appName: String): String? {
        return appPackages[appName]
    }
    
    /**
     * 지원하는 앱 목록 반환
     */
    fun getSupportedApps(): List<String> {
        return appPackages.keys.toList()
    }
    
    /**
     * 앱이 지원되는지 확인
     */
    fun isAppSupported(appName: String): Boolean {
        return appPackages.containsKey(appName)
    }
}
