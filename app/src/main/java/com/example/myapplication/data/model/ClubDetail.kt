
package com.example.myapplication.data.model

data class ClubDetail(
    val clubRegisProcess: String,
    val clubCancelIntroduction: List<String>,
    val clubQualification: List<String>,
    val clubIntroduction: String,
    val clubName: String,
    val clubIntro: String,
    val clubUUID: String,
    val clubNotice: String,
    val clubLogo: String,
    val clubPrice: Int,
    val memberCount: Int
)
