package com.example.myapplication.data.remote

import com.example.myapplication.data.DTO.Request.InitSignUpRequest
import com.example.myapplication.data.DTO.Request.MemberUpdateRequest
import com.example.myapplication.data.DTO.Response.MemberResponse
import com.example.myapplication.data.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface MemberAPIService {

    // 초기 회원가입
    @POST("api/v1/auth/signup")
    fun initSignUp(@Body request: InitSignUpRequest): Call<LoginResponse>

    // 회원 수정
    @PATCH("/api/v1/auth/update")
    fun updateMember(@Header("Authorization") token: String, @Body request: MemberUpdateRequest): Call<MemberResponse>

}