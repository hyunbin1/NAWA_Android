package com.example.myapplication.data.remote

import com.example.myapplication.data.DTO.Request.ClubBannerDTO
import com.example.myapplication.data.DTO.Request.LoginRequest
import com.example.myapplication.data.DTO.Response.LoginResponse
import com.example.myapplication.data.DTO.Response.MembershipResponse
import com.example.myapplication.data.database.Club
import com.example.myapplication.data.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // 클럽 리스트를 가져오는 api
    @GET("api/v1/clubs")
    fun getClubs(): Call<List<Club>>

    @GET("api/v1/clubs")
    fun getClubBanners(): Call<List<ClubBannerDTO>>


    // login 요청을 보내는 api
    @POST("api/v1/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // 클럽의 자세한 정보를 가져오는 api
    @GET("api/v1/clubs/{clubUUID}")
    fun getClubDetail(@Path("clubUUID") clubUUID: String): Call<Club>

    // 공지사항을 가져오는 api
    @GET("api/v1/notice")
    fun getNotices(): Call<List<Notice>>

    // 공지사항 세부 정보를 가져오는 api
    @GET("api/v1/notice/{noticeId}")
    fun getNoticeDetail(@Path("noticeId") noticeId: Int): Call<Notice>

    // 회원 정보를 가져오는 api
    @GET("api/v1/member/me")
    fun getMemberInfo(@Header("Authorization") token: String): Call<Member>

    @GET("api/v1/join/club/{clubUUID}")
    fun checkClubMembership(
        @Header("Authorization") token: String,
        @Path("clubUUID") clubUUID: String
    ): Call<List<MembershipResponse>>

    @POST("/review/{clubUUID}/create")
    fun createReviewWithLoginToken(
        @Header("Authorization") token: String,
        @Path("clubUUID") clubUUID: String,
        @Body review: String
    ): Call<String>

}
