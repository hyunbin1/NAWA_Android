package com.example.myapplication.data.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.data.database.enum.NotificationCategory

class NoticeDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "notices.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "notices"
        const val COLUMN_ID = "id"
        const val COLUMN_NOTICE_UUID = "noticeUUID"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PINNED = "pinned"
        const val COLUMN_PINNED_AT = "pinnedAt"
        const val COLUMN_VIEW_COUNT = "viewCount"
        const val COLUMN_CREATE_AT = "createAt"
        const val COLUMN_UPDATED_AT = "updatedAt"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NOTICE_UUID TEXT," +
                "$COLUMN_CATEGORY TEXT," + // Save the enum name as a string
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_CONTENT TEXT," +
                "$COLUMN_PINNED INTEGER," +
                "$COLUMN_PINNED_AT TEXT," +
                "$COLUMN_VIEW_COUNT INTEGER," +
                "$COLUMN_CREATE_AT TEXT," +
                "$COLUMN_UPDATED_AT TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}
