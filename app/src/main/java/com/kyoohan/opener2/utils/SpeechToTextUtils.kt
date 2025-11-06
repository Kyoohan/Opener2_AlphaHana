package com.kyoohan.opener2.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * STT(Speech-to-Text) 기능을 제공하는 유틸리티 클래스
 */
class SpeechToTextUtils(private val context: Context) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var onResultCallback: ((String) -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null
    
    /**
     * 음성 인식 권한이 있는지 확인
     */
    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 음성 인식 시작
     */
    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!hasPermission()) {
            onError("음성 인식 권한이 필요합니다.")
            return
        }
        
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("음성 인식이 지원되지 않습니다.")
            return
        }
        
        this.onResultCallback = onResult
        this.onErrorCallback = onError
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    // 음성 인식 준비 완료
                }
                
                override fun onBeginningOfSpeech() {
                    // 음성 입력 시작
                }
                
                override fun onRmsChanged(rmsdB: Float) {
                    // 음성 크기 변화 (볼륨 표시용)
                }
                
                override fun onBufferReceived(buffer: ByteArray?) {
                    // 음성 버퍼 수신
                }
                
                override fun onEndOfSpeech() {
                    // 음성 입력 종료
                }
                
                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "오디오 오류"
                        SpeechRecognizer.ERROR_CLIENT -> "클라이언트 오류"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "권한 부족"
                        SpeechRecognizer.ERROR_NETWORK -> "네트워크 오류"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 시간 초과"
                        SpeechRecognizer.ERROR_NO_MATCH -> "인식된 음성이 없습니다"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "음성 인식기 사용 중"
                        SpeechRecognizer.ERROR_SERVER -> "서버 오류"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "음성 입력 시간 초과"
                        else -> "알 수 없는 오류"
                    }
                    onErrorCallback?.invoke(errorMessage)
                }
                
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val recognizedText = matches[0]
                        onResultCallback?.invoke(recognizedText)
                    } else {
                        onErrorCallback?.invoke("인식된 음성이 없습니다.")
                    }
                }
                
                override fun onPartialResults(partialResults: Bundle?) {
                    // 부분 결과 (실시간 텍스트 표시용)
                }
                
                override fun onEvent(eventType: Int, params: Bundle?) {
                    // 추가 이벤트
                }
            })
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR") // 한국어 설정
            putExtra(RecognizerIntent.EXTRA_PROMPT, "말씀해 주세요...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        speechRecognizer?.startListening(intent)
    }
    
    /**
     * 음성 인식 중지
     */
    fun stopListening() {
        speechRecognizer?.stopListening()
    }
    
    /**
     * 음성 인식 취소
     */
    fun cancelListening() {
        speechRecognizer?.cancel()
    }
    
    /**
     * 리소스 정리
     */
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        onResultCallback = null
        onErrorCallback = null
    }
}

