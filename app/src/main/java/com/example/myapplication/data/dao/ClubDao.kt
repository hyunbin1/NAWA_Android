package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.data.model.Club
import com.example.myapplication.data.model.ClubDetail

@Dao
interface ClubDao {
    @Insert
    suspend fun insertClub(club: ClubDetail)

    @Query("SELECT * FROM club")
    suspend fun getAllClubs(): List<Club>
}
