//package com.example.myapplication.data.dao
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.example.myapplication.data.database.Notification
//
//@Dao
//interface NoticeDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertNotice(notice: Notification)
//
//    @Query("SELECT * FROM notice")
//    suspend fun getAllNotices(): List<Notification>
//}