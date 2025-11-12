package com.kyoohan.opener2.data

// 지도 API 요청/응답 모델들
data class MaplinkRequest(
    val query: String,
    val current: LocationHint? = null
)

data class LocationHint(
    val name: String? = null,
    val lat: Double? = null,
    val lng: Double? = null
)

data class PartsLocation(
    val name: String? = null,
    val lat: Double? = null,
    val lng: Double? = null
)

data class Parts(
    val mode: String,
    val origin: PartsLocation? = null,
    val destination: PartsLocation,
    val waypoints: List<PartsLocation> = emptyList()
)

data class MaplinkResponse(
    val url: String,
    val parts: Parts,
    val source: String
)

data class ErrorResponse(
    val error: String,
    val message: String? = null
)






































