package com.example.myapplication.data.DTO.Request

data class MemberUpdateRequest(
    val signUpStep: String? = null,
    val role: String? = null,
    val profileImage: String? = null,
    val nickname: String? = null,
    val name: String? = null,
    val phoneNumber: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val job: String? = null,
    val address: String? = null,
    val detailAddress: String? = null,
    val activeAddress: String? = null,
    val mbti: String? = null,
    val smoking: String? = null,
    val drinking: String? = null,
    val religion: String? = null,
    val hobby: String? = null,
    val introduce: String? = null,
    val answer1: String? = null,
    val answer2: String? = null,
    val answer3: String? = null,
    val answer4: String? = null
)
