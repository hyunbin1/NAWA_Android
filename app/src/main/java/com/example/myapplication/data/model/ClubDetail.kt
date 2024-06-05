// ClubDetail.kt
package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "club_detail")
data class ClubDetail(
    val clubRegisProcess: String,
    val clubCancelIntroduction: List<String>,
    val clubQualification: List<String>,
    val clubIntroduction: String,
    val clubName: String,
    val clubIntro: String,
    @PrimaryKey val clubUUID: String,
    val clubNotice: String,
    val clubLogo: String,
    val clubPrice: Int,
    val memberCount: Int
)
