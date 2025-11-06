plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// local.properties에서 API 키 읽기
val localProperties = java.util.Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(java.io.FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.kyoohan.opener2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kyoohan.opener2"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // local.properties에서 API 키 읽기 (선택사항)
        // 주의: 이 방법은 빌드 시점에 키가 포함되므로 완전히 안전하지 않습니다.
        // 프로덕션에서는 서버를 통한 인증을 권장합니다.
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${localProperties.getProperty("gemini.api.key", "")}\""
        )
        // Vertex AI tuned model API key - 학습된 딥링크 생성 모델 접근용
        // local.properties에 설정하지 않으면 기본값 사용 (공유된 학습 모델)
        buildConfigField(
            "String",
            "VERTEX_API_KEY",
            "\"${localProperties.getProperty("vertex.api.key", "AQ.Ab8RN6KTZarJqnZftcLqtI_vFmEEP4iBw_QebyQoT77rGMh4Zw")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    
    // ViewModel and Navigation
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    
    // Markdown support
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:html:4.6.2")
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation("io.noties.markwon:ext-tables:4.6.2")
    
    // Kakao SDK
    implementation("com.kakao.sdk:v2-all:2.20.6") // 모든 모듈
    implementation("com.kakao.sdk:v2-user:2.20.6") // 카카오 로그인
    implementation("com.kakao.sdk:v2-talk:2.20.6") // 카카오톡 메시지
    implementation("com.kakao.sdk:v2-share:2.20.6") // 카카오톡 공유
    
    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}