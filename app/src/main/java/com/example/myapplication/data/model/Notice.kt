package com.example.myapplication.data.model

data class Notice(
    val id: Int,
    val noticeUUID: String,
    val category: String,
    val title: String,
    val content: String,
    val pinned: Boolean,
    val pinnedAt: String?,
    val viewCount: Int,
    val createAt: String,
    val updatedAt: String
)
