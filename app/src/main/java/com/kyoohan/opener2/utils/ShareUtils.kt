package com.kyoohan.opener2.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Android 공유 기능 유틸리티
 */
object ShareUtils {
    private const val TAG = "ShareUtils"
    
    /**
     * 이미지를 Android 공유 액션으로 전송
     */
    fun shareImage(context: Context, imageUri: Uri) {
        try {
            // 모든 앱에 URI 권한 부여
            val packageManager = context.packageManager
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            // 공유 가능한 앱들에 권한 부여
            val resolveInfos = packageManager.queryIntentActivities(intent, 0)
            for (resolveInfo in resolveInfos) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            
            val chooserIntent = Intent.createChooser(intent, "이미지 공유")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            context.startActivity(chooserIntent)
            
            Log.d(TAG, "이미지 공유 화면 실행: $imageUri")
        } catch (e: Exception) {
            Log.e(TAG, "이미지 공유 실패", e)
            throw e
        }
    }
    
    /**
     * 텍스트를 Android 공유 액션으로 전송
     */
    fun shareText(context: Context, text: String) {
        try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            
            val chooserIntent = Intent.createChooser(shareIntent, "텍스트 공유")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            context.startActivity(chooserIntent)
            
            Log.d(TAG, "텍스트 공유 화면 실행: $text")
        } catch (e: Exception) {
            Log.e(TAG, "텍스트 공유 실패", e)
            throw e
        }
    }
    
    /**
     * 이미지와 텍스트를 함께 공유
     */
    fun shareImageWithText(context: Context, imageUri: Uri, text: String) {
        try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooserIntent = Intent.createChooser(shareIntent, "이미지 공유")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            context.startActivity(chooserIntent)
            
            Log.d(TAG, "이미지+텍스트 공유 화면 실행")
        } catch (e: Exception) {
            Log.e(TAG, "이미지+텍스트 공유 실패", e)
            throw e
        }
    }
}

