
package com.example.myapplication.data.DTO.Response

data class LoginResponse(
    val data: Data
) {
    data class Data(
        val signUpStep: String,
        val grantType: String,
        val accessToken: String, // 저장할 토큰
        val refreshToken: String,
        val accessTokenExpiresIn: Long
    )
}
