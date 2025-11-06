package com.kyoohan.opener2.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

/**
 * Content URI를 실제 파일로 변환하는 유틸리티
 */
object FileUtils {
    private const val TAG = "FileUtils"
    
    /**
     * Content URI를 임시 파일로 복사
     */
    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            // 임시 파일 생성
            val fileName = "kakao_temp_${System.currentTimeMillis()}.jpg"
            val tempFile = File(context.cacheDir, fileName)
            
            // Content URI에서 InputStream 가져오기
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            Log.d(TAG, "파일 변환 성공: ${tempFile.absolutePath}")
            tempFile
        } catch (e: Exception) {
            Log.e(TAG, "파일 변환 실패", e)
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 임시 파일 삭제
     */
    fun deleteTempFile(file: File) {
        try {
            if (file.exists()) {
                file.delete()
                Log.d(TAG, "임시 파일 삭제: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "임시 파일 삭제 실패", e)
        }
    }
}


