package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.data.DTO.Request.ClubBannerDTO
import com.example.myapplication.data.database.Club

@Dao
interface ClubDao {
    // 클럽을 데이터베이스에 삽입
    @Insert
    suspend fun insertClub(club: Club)

    // 모든 클럽 조회
    @Query("SELECT * FROM club")
    suspend fun getAllClubs(): List<Club>

    @Query("SELECT clubName, clubUUID, clubLogo FROM club")
    suspend fun getAllClubBanners(): List<ClubBannerDTO>


    // clubUUID를 이용하여 특정 클럽 조회
    @Query("SELECT * FROM club WHERE clubUUID = :uuid")
    suspend fun getClubByUUID(uuid: String): Club?
}