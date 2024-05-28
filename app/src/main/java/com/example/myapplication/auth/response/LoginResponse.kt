package com.example.myapplication.auth.response

data class LoginResponse(
    val data: Data
) {
    data class Data(
        val signUpStep: String,
        val grantType: String,
        val accessToken: String,
        val refreshToken: String,
        val accessTokenExpiresIn: Long
    )
}
