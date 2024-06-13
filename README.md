# NAWA_Android

![main](https://github.com/hyunbin1/NAWA_Android/blob/main/imgs/main.png?raw=true)
![main2](https://github.com/hyunbin1/NAWA_Android/blob/main/imgs/main2.png?raw=true)

## Commit Convention

- [Add] : 어떤 코드를 추가했을 때 추가한 내용
- [Fetch] : 기존 코드 수정 시
- [Delete] : 기존 코드 삭제 시
- [Comment] : 코드 리뷰

## Dependencies

- 외부 API 호출

```
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
```

- Room

```
plugins{
    id("kotlin-kapt")
}
android {
    implementation("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
}
```
