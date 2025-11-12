package com.kyoohan.opener2.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager

object AccessibilityUtils {
    fun isTalkBackEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return accessibilityManager.isTouchExplorationEnabled
    }
    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openGeneralSettings(context)
        }
    }

    fun openTalkBackSettings(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    val intent = Intent().apply {
                        action = "android.settings.ACCESSIBILITY_DETAILS_SETTINGS"
                        putExtra("android.provider.extra.COMPONENT_NAME", 
                            ComponentName(
                                "com.google.android.marvin.talkback",
                                "com.google.android.marvin.talkback.TalkBackService"
                            ).flattenToString())
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    
                    // Intent가 resolve 될 수 있는지 확인
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        Log.d("AccessibilityUtils", "Successfully opened TalkBack details settings")
                        return
                    } else {
                        Log.w("AccessibilityUtils", "ACTION_ACCESSIBILITY_DETAILS_SETTINGS intent cannot be resolved")
                    }
                } catch (e: Exception) {
                    Log.e("AccessibilityUtils", "Failed to open TalkBack details settings: ${e.message}", e)
                }
            }
            // 기본 접근성 설정 화면으로 fallback
            Log.d("AccessibilityUtils", "Falling back to general accessibility settings")
            openAccessibilitySettings(context)
        } catch (e: Exception) {
            Log.e("AccessibilityUtils", "Failed to open accessibility settings: ${e.message}", e)
            // 최종 fallback
            openGeneralSettings(context)
        }
    }
    
    /**
     * 일반 설정 화면으로 이동 (fallback)
     */
    private fun openGeneralSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // 최후의 fallback - 아무것도 하지 않음
        }
    }
    
    /**
     * TalkBack 서비스가 설치되어 있는지 확인
     */
    fun isTalkBackAvailable(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val services = accessibilityManager.getInstalledAccessibilityServiceList()
        return services.any { service ->
            service.id.contains("talkback", ignoreCase = true) ||
            service.id.contains("com.google.android.marvin.talkback", ignoreCase = true)
        }
    }
}













