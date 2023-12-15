# 👨‍👩‍👧‍👦 팀 남녀노소
## 😎 남녀노소의 멤버들을 소개합니다

|[K006_김민조](https://github.com/upsk1)|[K034_장지호](https://github.com/jangjh123)|[K039_조준장](https://github.com/junjange)|[J132_장민석](https://github.com/msjang4)|[J162_하채리](https://github.com/5tarry)|
|:---:|:---:|:---:|:---:|:---:|
|<img src="https://github.com/upsk1.png">|<img src="https://github.com/jangjh123.png">|<img src="https://github.com/junjange.png">|<img src="https://github.com/msjang4.png">|<img src="https://github.com/5tarry.png">|
|Android|Android|Android|Backend|Backend|

---

# 뭅뭅 - 댄스 숏폼 비디오 플랫폼
<p align="center">
  <img 
    src="https://github.com/boostcampwm2023/and03_movemove/assets/82919343/deb7977e-de08-4cab-977c-b4cf5d585d62"
    width="300"
    height="300"
  >
</p>
<p align="center">
  <a href="https:/github.com/boostcampwm2023/and03_movemove/wiki/">팀 위키</a>
</p>

### 앱 <뭅뭅>은 댄스 영상을 숏폼 비디오 형태로 시청하고 업로드할 수 있는 플랫폼입니다.
최근, 숏 폼 비디오 플랫폼이 빠르게 성장하고 있음에 기인하여 시작된 프로젝트입니다.

수많은 숏 폼 비디오 플랫폼 서비스가 존재하고, 해당 서비스들에서 많은 관심을 받는 비디오의 장르는 단연 **댄스**입니다.

저희 **남녀노소**는, `댄스 영상만 모아서 볼 수 있는 서비스를 만드는 것은 어떨까?` 라는 생각을 하게 되었고,

**경쟁적인 요소가 가미**되어, 개인 댄서에게는 `자기PR 의 기회`를, 학원이나 강사에겐 `홍보의 기회`를, 

인플루언서에게는 `팔로워 확보의 기회`를 제공할 수 있는 서비스, **뭅뭅**을 개발하게 되었습니다🥳

---

## 🗝 Key-Feature
### 동영상 시청
![영상재생](https://github.com/boostcampwm2023/and03_movemove/assets/82919343/118821c3-006a-4436-8ec1-3f01bdea2041)
### 동영상 점수 부여
![점수부여](https://github.com/boostcampwm2023/and03_movemove/assets/82919343/5b0804c6-e1b2-43fe-a898-aeb2a30acbff)
### 동영상 업로드
![upload](https://github.com/boostcampwm2023/and03_movemove/assets/82919343/c781fd25-8b59-4746-bf15-aab983762f6d)
---

## 📷 앱 스크린샷
- **스플래시 및 로그인 페이지**

<img width="30%" src="https://github.com/boostcampwm2023/and03_movemove/assets/69571848/bb80a8ff-7ec7-449c-9698-0e59d9d9e67b">   

- **회원가입 페이지**

<img width="30%" src="https://github.com/boostcampwm2023/and03_movemove/assets/69571848/2ef42055-b9f3-47e6-91ee-aedfd50a156f">      <img width="30%" src="https://github.com/boostcampwm2023/and03_movemove/assets/69571848/01d21650-fb78-49ac-978e-e4cea606e56d">

- **홈 페이지**

<img width="30%" src="https://github.com/boostcampwm2023/and03_movemove/assets/69571848/7d226035-6ce9-4fa8-8f7b-b5d754e8214e"> 

- **비디오 스트리밍 페이지**

<img width="30%" src="https://github.com/boostcampwm2023/and03_movemove/assets/69571848/9421e12b-bfe6-47bd-a803-69a5d9f30040">      <img width="30%" src="https://github.com/boostcampwm2023/and03_movemove/assets/69571848/f159f412-baf5-4c4d-9a18-7798899fed0d">

- **비디오 업로드 페이지**

<img width="30%" src="https://github.com/boostcampwm2023/and03_movemove/assets/69571848/61974afa-157b-497d-ae07-e3ebd35f78fd">

- **프로필 페이지**

<img width="30%" src="https://github.com/boostcampwm2023/and03_movemove/assets/69571848/7c69d788-6daf-4180-ae8e-b40f24f013fb">      <img width="30%" src="https://github.com/boostcampwm2023/and03_movemove/assets/69571848/c4b34e84-3382-4679-9a2d-7c325aab049f">



---

## 📱 뭅뭅 `Android` 팀의 도전

### 기술 스택

- **Minimum SDK Version** 26

- **Target SDK Version** 34

- **Clean Architecture**  지향

- **Architecture**
    - MVI

- **사용한 라이브러리**
    - Compose, Jetpack AAC, Hilt, Ktor, Coil, Media3  등

- **비동기 처리**
    - Coroutine Flow

### `K006_김민조`
### Compose
- 선언형 UI 
- 기존에는 개발할 때 XML 창을 반복하며 개발하였지만 컴포즈는 이러한 점이 없음
- 뷰에서 데이터를 구독시키기 위해 별다른 데이터 바인딩 작업이 없음

### MVI 패턴
- 기존 MVVM 상태 관리 문제와 부수효과로 예측 불가능한 단점을 극복
- 구현 난이도는 올라갔지만 인텐트 위주로 생각하기 때문에 로직은 간결해짐

### Clean Architecture
- ```Data Layer``` 에서 실제로 API통신에 사용할 소스 **Impl** 파일을 구현하고 Model 파일을 생성하여 Response 모델을 사용 
- ```Domain Layer``` 에서 ``` Data Layer```의 데이터를 필요에 따라 가공하고 **UseCase**를 만들어서 추후 ```Presentation Layer```에서 사용
- ```Presentation Layer``` 에서는 ```Domain Layer```에서 생성한 **UseCase**를 사용
- 이런 구조로 작성했을 시 추후 데이터받는 부분이 변경된다면 ```DataLayer```에서만 변경

### 소셜 로그인 API 
- **구글**,**카카오** 로그인 구현
- 인증 값으로 **웹 클라이언트 ID**와 **Android 키 해시**, **SHA-1 키** 관리

--- 
### `K034_장지호`
### `Jetpack Compose` 유저 인터랙션 경험
`Modifier` 의 `.pointerInput()` 메서드를 활용한 유저 인터랙션을 구현하였습니다. [이전에도 비슷한 구현을 한 경험](https://blothhundr.tistory.com/116)이 있으나,

이번 만큼 유저가 기대할만 한 모든 액션에 대해 처리하지는 않았습니다. 해당 과정을 통해 **`Unidirectional Data Flow` 구조에서**

**`ViewModel` 과 `UI` 가 어떠한 방식으로 소통하여야 하는지 제대로 파악**하였습니다. 자세한 구현 경험은 [여기](https://blothhundr.tistory.com/194)에서 확인하실 수 있습니다!

### 비디오 프레임 추출 기능의 로우 레벨 최적화
`MediaMetadataRetriever` 클래스를 통해 비디오 프레임을 추출하는 기존 구현에서 `MediaCodec` 을 활용하는 구현으로 리팩토링을 진행하였습니다.

해당 과정을 통해 `최대 약 62% 의 성능 개선` 을 이룰 수 있었으며, `CODEC`, `YUV` 등 기본적인 **안드로이드에서의 비디오 처리 관련 지식을 함양**할 수 있었습니다.

자세한 구현 경험은 [여기](https://blothhundr.tistory.com/199)에서 확인하실 수 있습니다!

### `Ktor` vs `Retrofit`
널리 사용되는 `Retrofit` 을 배제하고 `Ktor` 를 적용하였습니다. 모두에게 낯선 라이브러리였지만, **니즈가 확실**했기에 도전하게 되었습니다.

저는 해당 기술을 적극적으로 학습하고 프로젝트에 사용하였는데요. 편리하고 직관적인 사용을 위해 심도있는 추상화를 진행하였고, 사용에 관해

팀원들에게 전파하는 역할까지 수행하였습니다.

개인적으로 느끼기에는 `Retrofit` 보다 훨씬 직관적이고, 최초 구현을 마친 뒤에는 보일러 플레이트 코드가 줄어드는 경험을 가질 수 있었습니다.

구현에 대한 자세한 이야기는 [여기](https://blothhundr.tistory.com/193)에서 확인하실 수 있습니다!

---

### `K039_조준장`

### 비디오 스트리밍 최적화
- 많은 비디오를 쇼츠 형태로 효율적으로 보기 위해서 [ExoPlayer 비디오 스트리밍 최적화](https://github.com/boostcampwm2023/and03_movemove/wiki/%E2%9C%8F%EF%B8%8F-MoveMove-Tech-%EC%A1%B0%EC%A4%80%EC%9E%A5)를 진행함. 
- 최적화를 위해서는 두 가지 방식을 고려 
- 첫 번째 방식, 하나의 ExoPlayer를 생성하고 재활용하는 것으로, 메모리 사용량을 감소시키고 성능을 향상시킬 수 있는 장점이 있음. 그러나 스크롤 중에 영상 상태가 초기화되어 일시 정지나 재시작이 발생할 수 있는 단점이 있음 
- 두 번째 방식은 각각의 영상에 대해 새로운 ExoPlayer를 생성하는 것으로, 각 영상이 독립적으로 재생되고 전환 시 로딩 시간이 없는 장점이 있음. 그러나 메모리 사용량이 높아질 수 있는 단점이 있음. 
- 위 두가지 방식의 장단점을 극복하기 위해, 3개의 미리 생성된 ExoPlayer 인스턴스를 사용하여 최적화를 진행
- 3개의 ExoPlayer를 활용하여 이전 화면, 현재 화면, 그리고 다음 화면의 영상을 효율적으로 처리하면서 시스템 리소스를 효과적으로 활용
- 이 방식은 각각의 화면에서 독립적으로 영상을 처리하면서도 메모리를 효율적으로 활용하는 장점이 있음
- 또한, LaunchedEffect가 아닌 DisposableEffect를 활용하여, 컴포넌트가 처음 구성될 때 또는 uri가 변경될 때마다 새로운 미디어 소스를 설정하고 ExoPlayer를 초기화하도록 구현, DisposableEffect가 해제될 때 onDispose 블록에서 정의한 ExoPlayer를 종료하고 미디어 소스를 지우도록 했음 
- 최종적으로 CPU 사용량을 평균 42%에서 18%로 감소시켜 전체적으로 57% 개선되었음.
- 이러한 최적화를 통해 부드러운 비디오 재생과 사용자 경험을 향상시킬 수 있었음.

## 💻 뭅뭅 `Backend` 팀의 도전

### 아키텍처
![cloudcraft-removebg](https://github.com/boostcampwm2023/and03_movemove/assets/39575061/d13a671b-0bfc-4e2d-b248-6cc287554bd4)

### 기술스택
<img src="https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=ffffff"> <img src="https://img.shields.io/badge/Nest.js-E0234E?logo=NestJS&logoColor=white"/>
<img src="https://img.shields.io/badge/Yarn Berry-2C8EBB?logo=yarn&logoColor=ffffff">
<img src="https://img.shields.io/badge/MongoDB-114411?logo=mongodb">
<img src="https://img.shields.io/badge/Mongoose-114411?logo=mongodb">
<img src="https://img.shields.io/badge/FFmpeg-ffffff?style=flat&logo=ffmpeg&logoColor=118811">
<img src="https://img.shields.io/badge/Naver Cloud Platform-03C75A?logo=naver&logoColor=ffffff">
<img src="https://img.shields.io/badge/Docker-2496ED?&logo=Docker&logoColor=white">
<img src="https://img.shields.io/badge/GitHub Actions-444444?logo=github-actions">



### 스트리밍
- HLS 프로토콜 기반 동작
- 비디오를 실시간으로 스트리밍 할 수 있는 환경 구축
- 사용자 네트워크 상태에 가장 적절한 화질의 비디오

### 서버리스 인코딩
- 트래픽에 유동적인 대응
- 서버 관리의 부담 완화
- 대부분이 유휴 상태로 비용적 이득

### 관련 문서
<li><a href="https://movemove-backend.notion.site/VOD-Station-1-Cloud-Function-API-Gateway-3607d938c1344348a56f05d90f1c6730?pvs=4">VOD Station 대체 (1) Cloud Function, API Gateway</a></li>
<li><a href="https://movemove-backend.notion.site/VOD-Station-2-VPC-subnet-NAT-Gateway-fd2fd12f6c87439d863b0090f7897db5?pvs=4">VOD Station 대체 (2) VPC Subnet, NAT Gateway</a></li>
<li><a href="https://movemove-backend.notion.site/ffmpeg-1-fe829bdf2e64465899081270a69f8d2f?pvs=4">ffmpeg (1)</a></li>
<li><a href="https://movemove-backend.notion.site/Presigned-URL-6b2787a1bb734fbbb49e2ac4eb8c27f5?pvs=4">Presigned URL</a></li>
