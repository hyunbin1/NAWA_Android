package com.example.myapplication.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.myapplication.data.Converters
import com.example.myapplication.data.DTO.Request.ClubBannerDTO
import java.time.LocalDateTime

@Entity(tableName = "club")
@TypeConverters(Converters::class)
data class Club(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "club_id")
    val id: Long = 0,

    @ColumnInfo(name = "clubUUID", index = true)
    val clubUUID: String,

    @ColumnInfo(name = "clubName", index = true)
    val clubName: String,

    @ColumnInfo(name = "clubIntro")
    val clubIntro: String,

    @ColumnInfo(name = "clubLogo")
    val clubLogo: String? = null,

    @ColumnInfo(name = "clubIntroduction")
    val clubIntroduction: String? = null,

    @ColumnInfo(name = "clubQualification")
    val clubQualification: List<String>? = null,

    @ColumnInfo(name = "clubRegisProcess")
    val clubRegisProcess: String? = null,

    @ColumnInfo(name = "clubNotice")
    val clubNotice: String? = null,

    @ColumnInfo(name = "clubCancelIntroduction")
    val clubCancelIntroduction: List<String>? = null,

    @ColumnInfo(name = "clubPrice")
    val clubPrice: Int,

    @ColumnInfo(name = "memberCount")
    val memberCount: Int,

    @ColumnInfo(name = "createAt")
    val createAt: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "updatedAt")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    val isSqlite: Boolean = false
)

fun Club.toClubBannerRequest(): ClubBannerDTO {
    return ClubBannerDTO(
        clubUUID = this.clubUUID,
        clubName = this.clubName,
        clubLogo = this.clubLogo,
        isSqlite = this.isSqlite
    )
}
