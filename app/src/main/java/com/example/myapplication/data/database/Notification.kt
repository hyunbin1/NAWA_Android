package com.example.myapplication.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.data.database.enum.NotificationCategory

@Entity(tableName = "notice")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val noticeUUID: String,
    val category: NotificationCategory,
    val title: String,
    val content: String,
    val pinned: Boolean,
    val pinnedAt: String?,
    val viewCount: Int = 0,
    val createAt: String,
    val updatedAt: String?
)
