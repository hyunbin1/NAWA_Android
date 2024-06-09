package com.example.myapplication.data.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.data.database.Club
import java.time.LocalDateTime

class ClubDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                ${COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${COLUMN_CLUB_UUID} TEXT NOT NULL,
                ${COLUMN_CLUB_NAME} TEXT NOT NULL,
                ${COLUMN_CLUB_INTRO} TEXT NOT NULL,
                ${COLUMN_CLUB_LOGO} TEXT,
                ${COLUMN_CLUB_INTRODUCTION} TEXT,
                ${COLUMN_CLUB_QUALIFICATION} TEXT,
                ${COLUMN_CLUB_REGIS_PROCESS} TEXT,
                ${COLUMN_CLUB_NOTICE} TEXT,
                ${COLUMN_CLUB_CANCEL_INTRODUCTION} TEXT,
                ${COLUMN_CLUB_PRICE} INTEGER,
                ${COLUMN_MEMBER_COUNT} INTEGER,
                ${COLUMN_CREATE_AT} TEXT,
                ${COLUMN_UPDATED_AT} TEXT,
                ${COLUMN_IS_SQLITE} INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getClubById(id: Long): Club? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME, null, "$COLUMN_ID = ?", arrayOf(id.toString()),
            null, null, null
        )
        var club: Club? = null
        if (cursor.moveToFirst()) {
            club = Club(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                clubUUID = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLUB_UUID)),
                clubName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLUB_NAME)),
                clubIntro = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLUB_INTRO)),
                clubLogo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLUB_LOGO)),
                clubIntroduction = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLUB_INTRODUCTION)),
                clubQualification = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLUB_QUALIFICATION))?.split(","),
                clubRegisProcess = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLUB_REGIS_PROCESS)),
                clubNotice = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLUB_NOTICE)),
                clubCancelIntroduction = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLUB_CANCEL_INTRODUCTION))?.split(","),
                clubPrice = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLUB_PRICE)),
                memberCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEMBER_COUNT)),
                createAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATE_AT)).let { LocalDateTime.parse(it) },
                updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT)).let { LocalDateTime.parse(it) },
                isSqlite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_SQLITE)) == 1
            )
        }
        cursor.close()
        return club
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ClubDatabase.db"
        const val TABLE_NAME = "club"
        const val COLUMN_ID = "club_id"
        const val COLUMN_CLUB_UUID = "clubUUID"
        const val COLUMN_CLUB_NAME = "clubName"
        const val COLUMN_CLUB_INTRO = "clubIntro"
        const val COLUMN_CLUB_LOGO = "clubLogo"
        const val COLUMN_CLUB_INTRODUCTION = "clubIntroduction"
        const val COLUMN_CLUB_QUALIFICATION = "clubQualification"
        const val COLUMN_CLUB_REGIS_PROCESS = "clubRegisProcess"
        const val COLUMN_CLUB_NOTICE = "clubNotice"
        const val COLUMN_CLUB_CANCEL_INTRODUCTION = "clubCancelIntroduction"
        const val COLUMN_CLUB_PRICE = "clubPrice"
        const val COLUMN_MEMBER_COUNT = "memberCount"
        const val COLUMN_CREATE_AT = "createAt"
        const val COLUMN_UPDATED_AT = "updatedAt"
        const val COLUMN_IS_SQLITE = "isSqlite"
    }
}
