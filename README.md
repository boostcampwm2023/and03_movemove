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

### 앱 <뭅뭅>은 댄스 영상을 숏폼 비디오 형태로 시청하고 업로드할 수 있는 플랫폼입니다.
최근, 숏 폼 비디오 플랫폼이 빠르게 성장하고 있음에 기인하여 시작된 프로젝트입니다.

수많은 숏 폼 비디오 플랫폼 서비스가 존재하고, 해당 서비스들에서 많은 관심을 받는 비디오의 장르는 단연 **댄스**입니다.

저희 **남녀노소**는, `댄스 영상만 모아서 볼 수 있는 서비스를 만드는 것은 어떨까?` 라는 생각을 하게 되었고,

**경쟁적인 요소가 가미**되어, 개인 댄서에게는 `자기PR 의 기회`를, 학원이나 강사에겐 `홍보의 기회`를, 

인플루언서에게는 `팔로워 확보의 기회`를 제공할 수 있는 서비스, **뭅뭅**을 개발하게 되었습니다🥳

---

## 🗝 Key-Feature
### 동영상 시청
### 동영상 점수 부여
### 동영상 업로드

---

## 📷 앱 스크린샷

---

## 📱 뭅뭅 `Android` 팀의 도전
### `K006_김민조`
### Compose
- 제 첫번째 도전은 ```Compose``` 였습니다!
- 이전 학습 스프린트 과정에서 ```XML``` 뷰 시스템에서의 적응이 끝나기도 전에 ```Compose```를 하게 되었고 프로젝트 시작 전 팀원 분의 가르침과 Basics codelab을 참고하여 급히 공부하였습니다 ! (https://github.com/boostcampwm2023/and03_movemove/wiki/Compose-%EC%A0%81%EC%9D%91%EA%B8%B0)
- 자세하게 학습하여 정확히 사용한 건 아니라고 생각하고 있고 적어도 ```Compose```의 특징을 ```XML```과 비교하자면 여러가지 **편한점**을 느낄 수 있었습니다.
- **편한점**은 1.XML 창을 왔다갔다 안해도 되서 좋았다 2. 뷰에서 데이터를 구독할 때 데이터 바인딩을 사용 안해도 되서 편했다 정도를 느꼈습니다!

### MVI 패턴
- 이전에 ```MVVM``` 패턴에 대해 공부하던 과정에서 곧바로 ```MVI``` 패턴에 대해 사용하게 되었기에 이 또한 제게 큰 도전이였습니다!
- 기존에 팀장님 께서 셋업 해주신 코드로 사용을 하였고, ```MVI``` 패턴을 사용하면서 적어도 모든 행위(데이터 변경, 이펙트, 이벤트알림)를 **Intent** 로 사용하여 오히려 더 로직이 간결해 졌습니다.
- 다만 Contract 파일, ViewModel, View 세부분에서 소스를 각자 추가하였기에 소스를 작성할때 처음에 헷갈리는게 있지만 충분히 적응할만 하였다고 느꼈습니다.

### Clean Architecture
- 학습을 많이 못하여 아쉽지만 적어도 ```뭅뭅``` 프로젝트에서 레이어 구조를 이해하였습니다.
- ```Data Layer``` 에서 실제로 API통신에 사용할 소스 **Impl** 파일을 구현하고 Model 파일을 생성하여 Response 모델을 사용합니다 
- ```Domain Layer``` 에서 ``` Data Layer```의 데이터를 필요에 따라 가공하고 **UseCase**를 만들어서 추후 ```Presentation Layer```에서 사용할 함수를 작성합니다
- ```Presentation Layer``` 에서는 ```Data Layer```에서 생성한 **UseCase**를 사용합니다.
- 이런 구조로 작성했을 시 추후 데이터받는 부분이 변경된다면 ```DataLayer```에서만 변경하고 사용하면 된다는 점이 메리트로 느껴졌습니다!

### 소셜 로그인 API 
- **구글**,**카카오** 로그인 구현하면서 데이터를 요구할때의 인증 값으로 **웹 클라이언트 ID**와 **Android 키 해시**, **SHA-1 키** 관리를 해야 한다는 것을 새로 배웠습니다.

--- 
### `K034_장지호`
### `K039_조준장`

## 💻 뭅뭅 `Backend` 팀의 도전
### `J132_장민석`
### `J162_하채리`

---

## 📕 팀 문서
