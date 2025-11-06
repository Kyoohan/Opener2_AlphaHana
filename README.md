# 열림이 (Opener2) - 사회적 약자를 위한 AI 어시스턴트

열림이는 사회적 약자의 디지털 기기 사용을 돕는 AI 앱입니다. 복잡한 앱 조작 없이 자연어만으로 다양한 기능을 사용할 수 있도록 설계되었습니다.

**알파하나** 팀이 개발한 이 프로젝트는 사회적 약자의 정보 접근성 향상과 디지털 자립성 강화를 목표로 합니다.

## 프로젝트 목표

- 사회적 약자의 정보 접근성 향상
- 사회적 약자의 디지털 자립성 강화
- 포용적 기술 생태계 구축

## 주요 기능

### 1. 기본 AI & RAG (검색 증강 생성)
- **문제 해결**: 검색을 어떤 앱으로 해야 하는지 모르거나, 명확한 검색어 입력이 어려운 경우
- **기능**: 모호한 명령도 이해할 수 있는 AI와 검색 증강 생성 기능
- **구현**: Gemini API와 Google Search Grounding을 활용하여 최신 검색 결과를 AI 응답에 자동 포함
- **효과**: 복잡한 검색 앱 사용 없이 열림이 앱에서 최신 정보 획득 가능

### 2. 길찾기
- **문제 해결**: 길찾기를 어떤 앱으로 해야 하는지 모르거나, 어려운 길찾기 앱 사용법
- **기능**: 사용자 명령에 맞는 경로로 바로 안내하는 네이버 지도 딥링크 생성
- **구현**: Gemini API로 길찾기 의도 판별 → Vertex AI 튜닝 모델로 사용자 명령 추출 → 백엔드 서버에서 딥링크 생성
- **효과**: 복잡한 지도 앱 조작 없이 자연어만으로 길찾기 가능

### 3. 카카오톡 전송
- **문제 해결**: 카카오톡 설치 및 사용법 모름, 사진 전송하는 방법 모름
- **기능**: 카카오톡과 연동해 원하는 친구에게 메시지 및 사진 전송
- **구현**: AI 키워드 기반 의도 감지 → 메시지 추출 → Kakao SDK를 통한 친구 선택 후 전송
- **효과**: 카카오톡 사용법을 몰라도 메시지 전송 가능

### 4. 이미지 분석 및 공유
- **문제 해결**: 키오스크 및 앱 사용이 어려움, 물체가 궁금한 경우
- **기능**: 카메라로 사진 촬영 또는 이미지 첨부 시 이미지 분석/공유
- **구현**: 이미지를 Base64 인코딩하여 Gemini Vision API로 전송 후 분석, 의도에 따라 카카오 SDK 또는 Android 공유 선택
- **효과**: 궁금증이나 어려움을 사진 찍어 해결 가능, 간단하게 이미지 공유 가능

### 5. 앱 설치 지원
- **문제 해결**: 앱 설치 어려움
- **기능**: 앱 설치 요청 시 해당 앱 플레이스토어로 자동 이동
- **구현**: 키워드 기반 의도 감지 → 앱 이름 추출 → 앱 패키지명 리스트에서 매칭 → 플레이스토어 실행
- **효과**: 복잡한 앱 설치 과정 대신 자연어로 앱 설치 가능

### 6. 접근성 향상
- **대상**: 저시력자, 색약, 글자가 잘 안 보이시는 노인 분들, 시각 장애인
- **기능**: 
  - 고대비 모드 (WCAG AAA 가이드라인 준수)
  - 글자 크기 조절
  - TalkBack 기능 연동
  - 음성 인식 (Android SpeechRecognizer)
- **효과**: 저시력자의 가독성 향상, 노인의 정보 인식 용이성 향상, 음성 입력으로 입력 부담 감소

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

### 3. API 키 설정

**중요: API 키는 절대 공개 저장소에 커밋하지 마세요!**

프로젝트에는 하드코딩된 API 키가 포함되어 있지 않습니다. 다음 방법 중 하나를 선택하여 API 키를 설정하세요:

#### 방법 1: 앱 내에서 설정 (권장)
1. Android Studio에서 프로젝트 열기
2. 앱 실행 시 나타나는 API 키 입력 다이얼로그에 발급받은 API 키 입력
3. 또는 앱 내 설정에서 API 키 변경 가능

#### 방법 2: local.properties 파일 사용 (선택사항)
1. 프로젝트 루트 디렉토리에 `local.properties.example` 파일을 참고하여 `local.properties` 파일 생성
2. `local.properties` 파일에 다음 내용 추가:
   ```properties
   gemini.api.key=YOUR_GEMINI_API_KEY_HERE
   # vertex.api.key는 선택사항입니다 (기본값 사용 가능)
   vertex.api.key=YOUR_VERTEX_API_KEY_HERE
   ```
3. `local.properties` 파일은 이미 `.gitignore`에 포함되어 있어 Git에 커밋되지 않습니다.

**참고**: 
- `local.properties`를 사용하는 경우, 빌드 시점에 API 키가 앱에 포함되므로 완전히 안전하지 않습니다. 프로덕션 환경에서는 서버를 통한 인증을 권장합니다.
- **Vertex API 키**: 학습된 딥링크 생성 AI 모델에 접근하기 위한 키입니다. `local.properties`에 설정하지 않으면 기본값(공유된 학습 모델)이 자동으로 사용됩니다. 다른 사람들도 학습된 모델을 사용할 수 있도록 기본값이 제공됩니다.

### 4. 앱 실행
1. Android Studio에서 프로젝트 열기
2. API 키 설정 완료 후 앱 실행

## 사용법

### 기본 사용
1. 앱 실행 후 API 키 입력
2. 하단 입력창에 질문이나 요청 입력 (음성 입력도 가능)
3. 전송 버튼 클릭 또는 Enter 키로 메시지 전송
4. AI 응답 대기 및 확인

### 사용 예시
- 길찾기: "강남역으로 가는 길 알려줘"
- 카카오톡 전송: "친구에게 오늘 날씨 좋다고 보내줘"
- 이미지 분석: 사진을 첨부하고 "이게 뭐야?"라고 질문
- 앱 설치: "카카오톡 앱 설치해줘"
- 정보 검색: "오늘 날씨 어때?" (자동으로 최신 검색 결과 포함)

## 기술 스택

### 개발 환경
- **언어**: Kotlin
- **UI**: Jetpack Compose
- **아키텍처**: MVVM (Model-View-ViewModel)
- **네트워킹**: Retrofit + OkHttp
- **의존성 주입**: Manual DI
- **상태 관리**: StateFlow, Flow
- **데이터 저장**: SharedPreferences
- **권한 관리**: Android Activity Result API

### AI 및 서버
- **기본 AI**: Gemini API
- **RAG**: Google Search Grounding
- **AI 튜닝**: Vertex AI
- **백엔드 서버**: Cloud Run
- **이미지 분석**: Gemini Vision API

### 외부 연동
- **카카오톡**: Kakao SDK (v2-all, v2-user, v2-talk, v2-share)
- **지도**: 네이버 지도 딥링크
- **접근성**: Android SpeechRecognizer, TalkBack 연동

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

## 기대효과 및 사회적 기여

### 기대효과
- **사회적 약자의 정보 접근성 향상**: 복잡한 앱 조작 없이 자연어만으로 다양한 정보에 접근 가능
- **사회적 약자의 디지털 자립성 강화**: 타인의 도움 없이도 디지털 기기를 활용하여 일상생활 문제 해결 가능
- **포용적 기술 생태계 구축**: 모든 사람이 디지털 기술의 혜택을 누릴 수 있는 환경 조성

### 사회적 기여
열림이는 단순한 AI 어시스턴트가 아닌, 사회적 약자의 디지털 격차를 해소하고 정보 접근성을 향상시키는 도구입니다. 특히 고령자, 저시력자, 시각 장애인 등 다양한 사회적 약자들이 디지털 기기를 더 쉽고 편리하게 사용할 수 있도록 돕습니다.

## API 사용량 및 비용

Google Cloud Service API는 사용량에 따라 비용이 발생할 수 있습니다. 자세한 가격 정보는 [Google Cloud Pricing](https://cloud.google.com/pricing)을 참조하세요.

## 윤리적 고려사항

### 개인정보 보호
- 사용자 정보 저장 시 개인정보 수집 및 보호 기준 마련
- 민감한 정보의 안전한 처리 및 저장

### AI 환각 문제
- RAG 기반 AI의 잘못된 정보 생성 가능성 인지
- 사용자에게 정보의 정확성을 확인하도록 안내

### AI 편향
- AI의 학습 데이터에 사회적 약자의 비중이 적을 수 있음을 인지
- 지속적인 모델 개선 및 편향 완화 노력

### AI 과의존 방지
- '사용자를 대체하는 AI'가 아닌 '사용자의 문제 수행을 돕는 AI'로 설계
- 사용자의 자립성과 학습을 지원하는 방향으로 개발

## 보안 고려사항

### API 키 보호
- **하드코딩된 API 키 제거**: 코드에서 하드코딩된 API 키를 모두 제거했습니다
- **.gitignore 설정**: `local.properties` 파일이 Git에 커밋되지 않도록 설정되어 있습니다
- **예제 파일 제공**: `local.properties.example` 파일을 통해 설정 방법을 안내합니다

### 추가 보안 권장사항
- API 키는 SharedPreferences에 암호화되지 않은 상태로 저장됩니다
- 프로덕션 환경에서는 더 안전한 저장 방법을 고려해야 합니다
- API 키는 절대 공개 저장소에 커밋하지 마세요
- 프로덕션 앱에서는 서버를 통한 인증 방식을 사용하는 것을 강력히 권장합니다
- Google Cloud Console에서 API 키에 대한 사용 제한을 설정하세요 (IP 주소, 앱 패키지명 등)

## 한계점 및 개선 방안

### 현재 한계점
1. **지원 앱의 제한**: AI가 모든 앱을 다루지 못함
2. **플랫폼 제한**: 현재 Android만 지원
3. **자연어 및 음성 인식 한계**: 복잡한 명령이나 특정 방언 인식의 어려움
4. **AI 튜닝 모델의 정확도**: 다양한 상황에 대한 대응 한계

### 개선 방안
1. **지원 앱 확대**: 더 많은 앱과 서비스 연동
2. **iOS 지원**: iOS 버전 개발을 통한 플랫폼 확장
3. **추가 학습 및 사용자 음성 프로필**: 개인 맞춤형 음성 인식 기능 강화
4. **다채로운 데이터로 튜닝 강화**: 더 다양한 시나리오와 데이터로 AI 모델 개선

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






















