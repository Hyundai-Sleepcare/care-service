# NEST

잠든 동승자의 좌석·공조·조명 설정을 가족별 프로필로 저장해 두고, 좌석을 골라 한 번에 슬립 모드를 실행하는 차량용 앱입니다.

> 2026 현대자동차·기아 차량용 앱 개발 공모전 · 패밀리·동승자 케어 분야 · 팀 네가자는모습만봐도배불러

## APK

| 항목 | 내용 |
| --- | --- |
| 파일 위치 | [`apk/네가자는모습만봐도배불러_NEST_release.apk`](apk/네가자는모습만봐도배불러_NEST_release.apk) |
| 빌드 | release (서명됨) |
| 패키지명 | `com.hyundai.ngmb` |
| 지원 Android | minSdk 28 (Android 9) ~ targetSdk 36 |

## 실행 방법

### 앱 (APK 설치)

1. Android 에뮬레이터를 실행합니다.
2. APK를 에뮬레이터 화면에 끌어다 놓거나, 아래 명령으로 설치합니다.

   ```bash
   adb install apk/네가자는모습만봐도배불러_NEST_release.apk
   ```

3. 앱 목록에서 **NEST**를 실행합니다.

같은 패키지의 debug 빌드가 이미 설치돼 있으면 서명이 달라 설치가 실패합니다. 이때는 `adb uninstall com.hyundai.ngmb`로 기존 앱을 지운 뒤 설치합니다.

### 서버 (선택)

APK는 배포된 서버(Railway)를 사용하므로 서버를 따로 실행하지 않아도 됩니다. 로컬에서 서버를 실행하려면 Java 21과 MySQL이 필요합니다.

```bash
cd server
DB_USERNAME=<사용자> DB_PASSWORD=<비밀번호> ./gradlew bootRun --args='--spring.profiles.active=local'
```

- 로컬 DB: `jdbc:mysql://localhost:3306/sleepcare`
- API 문서(Swagger): `http://localhost:8080/swagger-ui/index.html`

## 테스트 환경 및 필요한 설정

| 항목 | 내용 |
| --- | --- |
| 개발·테스트 에뮬레이터 | Android Studio Emulator 37.2.13 · Pixel Tablet AVD |
| 시스템 이미지 | Android 16 (API 36), Google Play, arm64-v8a |
| 화면 규격 | 2560 × 1600 px, 320 dpi (가로 태블릿) |
| 서버 | Railway 배포 서버 `https://backend-production-910889bb.up.railway.app` |
| 필요한 설정 | 인터넷 연결 외 별도 설정 없음 |

- **Pleos Connect 에뮬레이터에서는 아직 검증하지 않았습니다.** 위 Android Studio 에뮬레이터에서 개발·테스트했습니다.
- 앱은 처음 실행할 때 기기 식별용 UUID를 만들어 저장하고, 이 값(`X-Owner-Id`)으로 서버에서 자신의 프로필만 조회·수정합니다.
- 서버에 연결하지 못하면 화면 확인용 기본 프로필이 표시됩니다.

## 구현 범위

### 현재 시제품에서 확인할 수 있는 기능

- 좌석 선택 (운전석은 운전자 보호를 위해 선택 불가)
- 가족별 프로필 생성·이름 변경·삭제 (최대 10개)
- 프리셋 편집: 등받이 각도, 시트 열선/통풍, 목표 온도, 무드 조명
- 프로필·프리셋 서버 저장 및 재조회
- 좌석별 슬립 모드 실행·종료 화면

### 개발 예정 기능

- Pleos Vehicle SDK를 통한 실제 좌석·공조·조명 제어
- 슬립 모드 시작 전 설정 기록과 종료 시 복원
- 운전자가 직접 바꾼 설정 보존
- 좌석과 프로필 연결 정보의 기기 저장
- 음성 인식을 통한 슬립 모드 실행·종료

현재 슬립 모드 실행·종료는 앱 안의 좌석별 실행 상태만 바꾸며, 차량에는 제어 요청을 보내지 않습니다.

## 저장소 구조

```
apk/      제출용 release APK
android/  Android 앱 (Kotlin, Jetpack Compose)
server/   프로필 저장 서버 (Spring Boot, MySQL)
```
