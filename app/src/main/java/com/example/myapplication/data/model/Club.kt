package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "club")
data class Club(
    val clubName: String,   // 클럽 이름
    @PrimaryKey val clubUUID: String,   // 클럽 id(pk)
    val clubLogo: String    // 클럽 로고 이미지
)
