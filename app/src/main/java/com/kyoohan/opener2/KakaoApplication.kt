package com.kyoohan.opener2

import android.app.Application
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import java.security.MessageDigest

class KakaoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Kakao SDK 초기화
        // Native App Key를 사용하여 초기화
        KakaoSdk.init(this, "3f689a8d995f1a125e8df94ae8968000")
        
        // 키 해시 출력 (개발 중에만 사용)
        printKeyHash()
    }
    
    /**
     * 키 해시를 Logcat에 출력합니다.
     * 이 키 해시를 복사해서 Kakao Developers 콘솔에 등록해야 합니다.
     */
    @Suppress("DEPRECATION")
    private fun printKeyHash() {
        try {
            Log.d("KakaoKeyHash", "패키지명: $packageName")
            println("========================================")
            println("📦 패키지명: $packageName")
            println("========================================")
            
            val info = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }
            
            val signatures = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                info.signingInfo?.let { signingInfo ->
                    if (signingInfo.hasMultipleSigners()) {
                        Log.d("KakaoKeyHash", "Multiple signers detected")
                        signingInfo.apkContentsSigners
                    } else {
                        signingInfo.signingCertificateHistory
                    }
                }
            } else {
                info.signatures
            }
            
            signatures?.forEachIndexed { index, signature ->
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                
                Log.d("KakaoKeyHash", "KeyHash[$index]: $keyHash")
                println("========================================")
                println("🔑 Kakao Key Hash #${index + 1}: $keyHash")
                println("이 키 해시를 Kakao Developers에 등록하세요!")
                println("========================================")
            }
            
            // SHA-1도 출력 (참고용)
            signatures?.firstOrNull()?.let { signature ->
                val md = MessageDigest.getInstance("SHA1")
                md.update(signature.toByteArray())
                val sha1 = md.digest().joinToString(":") { "%02X".format(it) }
                Log.d("KakaoKeyHash", "SHA-1 Fingerprint: $sha1")
                println("========================================")
                println("🔐 SHA-1 (참고): $sha1")
                println("========================================")
            }
        } catch (e: Exception) {
            Log.e("KakaoKeyHash", "Error getting key hash", e)
            e.printStackTrace()
        }
    }
}

