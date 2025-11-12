package com.kyoohan.opener2.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "opener_prefs"
        private const val API_KEY = "api_key"
        private const val VERTEX_API_KEY = "vertex_api_key"
        private const val FONT_SIZE_SCALE = "font_size_scale"
        private const val HIGH_CONTRAST_MODE = "high_contrast_mode"
        private const val ACCENT_COLOR_PRESET = "accent_color_preset"
        
        const val DEFAULT_FONT_SIZE_SCALE = 1.2f
        const val DEFAULT_HIGH_CONTRAST_MODE = false
        const val DEFAULT_ACCENT_COLOR_PRESET = 0 // 0=Warm, 1=Cool, 2=Purple, 3=Green
    }
    
    fun saveApiKey(apiKey: String) {
        prefs.edit().putString(API_KEY, apiKey).apply()
    }
    
    fun getApiKey(): String? {
        return prefs.getString(API_KEY, null)
    }
    
    fun clearApiKey() {
        prefs.edit().remove(API_KEY).apply()
    }
    
    fun saveVertexApiKey(apiKey: String) {
        prefs.edit().putString(VERTEX_API_KEY, apiKey).apply()
    }
    
    fun getVertexApiKey(): String? {
        return prefs.getString(VERTEX_API_KEY, null)
    }
    
    fun clearVertexApiKey() {
        prefs.edit().remove(VERTEX_API_KEY).apply()
    }
    
    // 글자 크기 설정
    // UI에서는 1.0~2.0 범위로 표시하지만, 실제로는 1.2~2.4에 매핑
    fun saveFontSizeScale(scale: Float) {
        // UI scale(1.0~2.0)을 실제 scale(1.2~2.4)로 변환
        val actualScale = scale * 1.2f
        prefs.edit().putFloat(FONT_SIZE_SCALE, actualScale).apply()
    }
    
    fun getFontSizeScale(): Float {
        val actualScale = prefs.getFloat(FONT_SIZE_SCALE, DEFAULT_FONT_SIZE_SCALE)
        // 실제 scale(1.2~2.4)을 UI scale(1.0~2.0)으로 변환
        return actualScale / 1.2f
    }
    
    // 실제 스케일 값을 가져오는 함수 (UI 요소에 적용할 때 사용)
    fun getActualFontSizeScale(): Float {
        val uiScale = getFontSizeScale()
        return uiScale * 1.2f
    }
    
    // 고대비 모드 설정
    fun saveHighContrastMode(enabled: Boolean) {
        prefs.edit().putBoolean(HIGH_CONTRAST_MODE, enabled).apply()
    }
    
    fun getHighContrastMode(): Boolean {
        return prefs.getBoolean(HIGH_CONTRAST_MODE, DEFAULT_HIGH_CONTRAST_MODE)
    }
    
    // Accent Color Preset 설정
    fun saveAccentColorPreset(preset: Int) {
        prefs.edit().putInt(ACCENT_COLOR_PRESET, preset).apply()
    }
    
    fun getAccentColorPreset(): Int {
        return prefs.getInt(ACCENT_COLOR_PRESET, DEFAULT_ACCENT_COLOR_PRESET)
    }
}






















