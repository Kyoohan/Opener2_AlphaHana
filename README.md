# 열림이 (Opener2) - 사회적 약자를 위한 AI 어시스턴트

열림이는 사회적 약자를 도와주는 AI 채팅 앱입니다. ChatGPT나 Gemini와 유사한 인터페이스를 제공하며, Google Cloud Service API를 통해 AI 응답을 받아옵니다.

## 주요 기능

- 🤖 **AI 채팅**: Google Cloud Service API를 통한 실시간 AI 응답
- 🎨 **모던 UI**: ChatGPT 스타일의 다크 테마 인터페이스
- 📝 **마크다운 지원**: AI 응답의 마크다운 형식 렌더링
- 💾 **API 키 저장**: 안전한 API 키 저장 및 관리
- 📱 **반응형 디자인**: 다양한 화면 크기에 최적화

## 설치 및 설정

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd Opener2
```

### 2. Google Cloud Service API 키 발급
1. [Google Cloud Console](https://console.cloud.google.com/)에 접속
2. 새 프로젝트 생성 또는 기존 프로젝트 선택
3. "APIs & Services" > "Library"에서 "Generative Language API" 활성화
4. "APIs & Services" > "Credentials"에서 API 키 생성
5. API 키를 안전하게 보관

### 3. 앱 실행
1. Android Studio에서 프로젝트 열기
2. API 키 설정:
   - 앱 실행 시 나타나는 API 키 입력 다이얼로그에 발급받은 API 키 입력
   - 또는 앱 내 설정에서 API 키 변경 가능

## 사용법

### 기본 사용
1. 앱 실행 후 API 키 입력
2. 하단 입력창에 질문이나 요청 입력
3. 전송 버튼 클릭 또는 Enter 키로 메시지 전송
4. AI 응답 대기 및 확인

### 추천 질문
- 📍 **지도 길찾기**: "지도 길찾기를 도와드릴게요. 어디로 가고 싶으신가요?"
- 💊 **주변 약국 찾기**: "주변 약국을 찾아드릴게요. 현재 위치를 알려주세요."
- 🧾 **공과금 확인하기**: "공과금 확인을 도와드릴게요. 어떤 공과금을 확인하고 싶으신가요?"

## 기술 스택

- **언어**: Kotlin
- **UI**: Jetpack Compose
- **아키텍처**: MVVM (Model-View-ViewModel)
- **네트워킹**: Retrofit + OkHttp
- **의존성 주입**: Manual DI
- **상태 관리**: StateFlow
- **데이터 저장**: SharedPreferences

## 프로젝트 구조

```
app/src/main/java/com/kyoohan/opener2/
├── data/                    # 데이터 모델
│   ├── ChatMessage.kt      # 채팅 메시지 모델
│   └── GeminiApiModels.kt  # API 응답 모델
├── network/                 # 네트워킹
│   ├── GeminiApiService.kt # API 서비스 인터페이스
│   └── NetworkModule.kt    # 네트워크 설정
├── repository/              # 데이터 저장소
│   └── ChatRepository.kt   # 채팅 데이터 관리
├── ui/                      # UI 컴포넌트
│   ├── components/          # 재사용 가능한 컴포넌트
│   ├── screens/            # 화면 컴포넌트
│   └── theme/              # 테마 설정
├── utils/                   # 유틸리티
│   └── PreferencesManager.kt # 설정 저장
├── viewmodel/               # 뷰모델
│   └── ChatViewModel.kt    # 채팅 뷰모델
└── MainActivity.kt         # 메인 액티비티
```

## 주요 컴포넌트

### ChatScreen
메인 채팅 화면으로 다음 기능을 제공합니다:
- 환영 화면 및 추천 질문
- 채팅 메시지 목록
- 메시지 입력 및 전송
- 로딩 상태 표시

### ChatBubble
채팅 메시지를 표시하는 컴포넌트:
- 사용자/AI 메시지 구분
- 마크다운 렌더링 지원
- 반응형 디자인

### MarkdownText
AI 응답의 마크다운을 렌더링하는 컴포넌트:
- 제목 (H1, H2, H3)
- 굵은 글씨, 기울임 글씨
- 인라인 코드
- 링크
- 목록

## API 사용량 및 비용

Google Cloud Service API는 사용량에 따라 비용이 발생할 수 있습니다. 자세한 가격 정보는 [Google Cloud Pricing](https://cloud.google.com/pricing)을 참조하세요.

## 보안 고려사항

- API 키는 SharedPreferences에 암호화되지 않은 상태로 저장됩니다
- 프로덕션 환경에서는 더 안전한 저장 방법을 고려해야 합니다
- API 키는 절대 공개 저장소에 커밋하지 마세요

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.






















