//package com.example.myapplication.data.database
//
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import androidx.room.TypeConverters
//import androidx.room.ColumnInfo
//import com.example.myapplication.data.Converters
//import com.example.myapplication.data.database.enum.NotificationCategory
//import java.time.LocalDateTime
//
//@Entity(tableName = "notification")
//@TypeConverters(Converters::class)
//data class Notification(
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "notice_id")
//    val id: Long = 0,
//
//    @ColumnInfo(name = "noticeUUID")
//    val noticeUUID: String? = null,
//
//    @ColumnInfo(name = "category")
//    val category: NotificationCategory? = null,
//
//    @ColumnInfo(name = "title")
//    val title: String? = null,
//
//    @ColumnInfo(name = "content")
//    val content: String? = null,
//
//    @ColumnInfo(name = "viewCount")
//    val viewCount: Int = 0,
//
//    @ColumnInfo(name = "createAt")
//    val createAt: LocalDateTime = LocalDateTime.now(),
//
//    @ColumnInfo(name = "updatedAt")
//    val updatedAt: LocalDateTime = LocalDateTime.now()
//)
