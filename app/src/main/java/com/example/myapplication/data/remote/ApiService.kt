
package com.example.myapplication.data.remote

import com.example.myapplication.data.model.Club
import com.example.myapplication.data.model.ClubDetail
import com.example.myapplication.data.model.LoginRequest
import com.example.myapplication.data.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/v1/clubs")
    fun getClubs(): Call<List<Club>>

    @POST("api/v1/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/v1/clubs/{clubUUID}")
    fun getClubDetail(@Path("clubUUID") clubUUID: String): Call<ClubDetail>
}
