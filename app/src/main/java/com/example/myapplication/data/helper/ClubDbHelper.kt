package com.example.myapplication.data.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ClubDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "clubs.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "club"
        const val COLUMN_ID = "id"
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
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CLUB_UUID TEXT NOT NULL,
                $COLUMN_CLUB_NAME TEXT NOT NULL,
                $COLUMN_CLUB_INTRO TEXT NOT NULL,
                $COLUMN_CLUB_LOGO TEXT,
                $COLUMN_CLUB_INTRODUCTION TEXT,
                $COLUMN_CLUB_QUALIFICATION TEXT,
                $COLUMN_CLUB_REGIS_PROCESS TEXT,
                $COLUMN_CLUB_NOTICE TEXT,
                $COLUMN_CLUB_CANCEL_INTRODUCTION TEXT,
                $COLUMN_CLUB_PRICE INTEGER,
                $COLUMN_MEMBER_COUNT INTEGER,
                $COLUMN_CREATE_AT TEXT,
                $COLUMN_UPDATED_AT TEXT
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
