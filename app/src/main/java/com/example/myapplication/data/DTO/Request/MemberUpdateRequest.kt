package com.example.myapplication.data.DTO.Request

data class MemberUpdateRequest (
    val signUpStep: String,
    val role: String,
    val profileImage: String,
    val nickname: String,
    val age: Int,
    val mbti: String,
    val hobby: String,
    val job: String
)