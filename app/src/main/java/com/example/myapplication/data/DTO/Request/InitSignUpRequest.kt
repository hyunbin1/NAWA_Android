package com.example.myapplication.data.DTO.Request

data class InitSignUpRequest (
    val emailId: String,
    val password: String,
    val marketingAgree: Boolean
)
