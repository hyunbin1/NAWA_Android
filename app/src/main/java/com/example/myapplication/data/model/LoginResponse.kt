
package com.example.myapplication.data.model

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
