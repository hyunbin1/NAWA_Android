//package com.example.myapplication.data.dao
//
//
//import androidx.room.*
//import com.example.myapplication.data.database.Notification
//
//@Dao
//interface NotificationDao {
//    @Query("SELECT * FROM notification ORDER BY createAt DESC")
//    suspend fun getAllNotifications(): List<Notification>
//
//    @Query("SELECT * FROM notification WHERE notice_id = :id")
//    suspend fun getNotificationById(id: Long): Notification?
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertNotification(notification: Notification)
//
//    @Update
//    suspend fun updateNotification(notification: Notification)
//
//    @Delete
//    suspend fun deleteNotification(notification: Notification)
//}