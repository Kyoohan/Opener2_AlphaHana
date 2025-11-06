package com.kyoohan.opener2.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.kyoohan.opener2.data.LocationHint
import com.kyoohan.opener2.data.MaplinkRequest
import com.kyoohan.opener2.network.MapApiClient
import retrofit2.HttpException

/**
 * 지도 API를 호출하여 네이버 지도 URL을 가져옵니다
 */
suspend fun getMapUrlOrThrow(query: String, current: LocationHint? = null): String {
    return MapApiClient.api.maplink(MaplinkRequest(query, current)).url
}

/**
 * 네이버 지도 앱을 열거나, 미설치 시 Play 스토어로 이동합니다
 */
fun openNaverMap(context: Context, nmapUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(nmapUrl)).apply {
        setPackage("com.nhn.android.nmap") // 네이버 지도 앱 우선
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // 미설치 → Play 스토어로
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.nmap"))
        )
    }
}

/**
 * 길찾기 쿼리에서 에러 메시지를 추출합니다
 */
fun getErrorMessage(exception: Exception): String {
    return when (exception) {
        is HttpException -> {
            when (exception.code()) {
                400 -> "목적지를 더 정확히 알려주세요. 예: '서울역 1호선' 또는 '서울역 5번 출구'"
                500 -> "잠시 후 다시 시도해주세요."
                else -> "길찾기 준비에 실패했어요. 다시 말씀해 주세요."
            }
        }
        else -> "네트워크 오류가 발생했어요."
    }
}

/**
 * 자연어에서 길찾기 의도를 감지합니다
 * 주의: 이 함수는 더 이상 사용되지 않습니다. 
 * Vertex AI 모델이 의도를 자동으로 감지합니다.
 */
@Deprecated("Use Vertex AI model for intent detection instead")
fun isNavigationQuery(message: String): Boolean {
    val navigationKeywords = listOf(
        "길찾기", "길 찾기", "가다", "가기", "가는길", "가는 길",
        "대중교통", "지하철", "버스", "운전", "도보", "자전거",
        "역", "정류장", "출구", "번출구", "호선", "선",
        "어떻게", "어디", "어느", "경로", "루트"
    )
    
    return navigationKeywords.any { keyword ->
        message.contains(keyword, ignoreCase = true)
    }
}










