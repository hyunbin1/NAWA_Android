package com.example.myapplication.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.data.database.Club
import com.example.myapplication.data.helper.ClubDbHelper
import com.example.myapplication.databinding.ActivityCreateClubBinding
import java.util.*

class CreateClubActivity : AppCompatActivity() {

    private lateinit var dbHelper: ClubDbHelper
    private lateinit var binding: ActivityCreateClubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateClubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = ClubDbHelper(this)

        binding.createClubButton.setOnClickListener {
            val clubName = binding.clubNameEditText.text.toString()
            val clubIntroduce = binding.clubDescriptionEditText.text.toString()
            val clubJoinCondition = binding.newClubCondition.text.toString()

            if (clubName.isEmpty() || clubIntroduce.isEmpty() || clubJoinCondition.isEmpty()) {
                Toast.makeText(this, "모든 필드를 작성해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val club = Club(
                    id = 0,
                    clubUUID = UUID.randomUUID().toString(),
                    clubName = clubName,
                    clubIntro = clubIntroduce,
                    clubQualification = listOf(clubJoinCondition),
                    clubLogo = R.drawable.ic_launcher_background.toString(),
                    clubIntroduction = clubIntroduce,
                    clubRegisProcess = "",
                    clubNotice = "",
                    clubCancelIntroduction = listOf(),
                    clubPrice = 0,
                    memberCount = 0,
                    isSqlite = true,
                    googleForm = "https://forms.gle/9d6rS5fFnPL8aUmY9"
                )
                saveClubToDatabase(club)
            }
        }
    }

    private fun saveClubToDatabase(club: Club) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(ClubDbHelper.COLUMN_CLUB_UUID, club.clubUUID)
            put(ClubDbHelper.COLUMN_CLUB_NAME, club.clubName)
            put(ClubDbHelper.COLUMN_CLUB_INTRO, club.clubIntro)
            put(ClubDbHelper.COLUMN_CLUB_LOGO, club.clubLogo)
            put(ClubDbHelper.COLUMN_CLUB_INTRODUCTION, club.clubIntroduction)
            put(ClubDbHelper.COLUMN_CLUB_QUALIFICATION, club.clubQualification?.joinToString(","))
            put(ClubDbHelper.COLUMN_CLUB_REGIS_PROCESS, club.clubRegisProcess)
            put(ClubDbHelper.COLUMN_CLUB_NOTICE, club.clubNotice)
            put(ClubDbHelper.COLUMN_CLUB_CANCEL_INTRODUCTION, club.clubCancelIntroduction?.joinToString(","))
            put(ClubDbHelper.COLUMN_CLUB_PRICE, club.clubPrice)
            put(ClubDbHelper.COLUMN_MEMBER_COUNT, club.memberCount)
            put(ClubDbHelper.COLUMN_CREATE_AT, club.createAt.toString())
            put(ClubDbHelper.COLUMN_UPDATED_AT, club.updatedAt.toString())
            put(ClubDbHelper.COLUMN_IS_SQLITE, if (club.isSqlite) 1 else 0)
        }

        val newRowId = db.insert(ClubDbHelper.TABLE_NAME, null, values)

        if (newRowId != -1L) {
            Toast.makeText(this, "클럽이 생성되었습니다.", Toast.LENGTH_SHORT).show()
            Log.d("CreateClubActivity", "클럽 생성 성공: ID = $newRowId")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "클럽 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
            Log.d("CreateClubActivity", "클럽 생성 실패")
        }
    }

}
