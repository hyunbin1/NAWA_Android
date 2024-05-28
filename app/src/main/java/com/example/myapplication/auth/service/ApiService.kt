package com.example.myapplication.auth.service

import com.example.myapplication.auth.request.LoginRequest
import com.example.myapplication.auth.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("api/v1/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
