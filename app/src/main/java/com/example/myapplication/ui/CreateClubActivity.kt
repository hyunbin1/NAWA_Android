package com.example.myapplication.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.model.ClubDetail
import com.example.myapplication.databinding.ActivityCreateClubBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class CreateClubActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateClubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateClubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createClubButton.setOnClickListener {
            val clubName = binding.clubNameEditText.text.toString()
            val clubDescription = binding.clubDescriptionEditText.text.toString()

            if (clubName.isNotEmpty() && clubDescription.isNotEmpty()) {
                createClub(clubName, clubDescription)
            } else {
                Toast.makeText(this, "모든 필드를 채워주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createClub(clubName: String, clubDescription: String) {
        val clubUUID = UUID.randomUUID().toString()
        val club = ClubDetail(
            clubRegisProcess = "등록 절차",
            clubCancelIntroduction = listOf("취소 소개"),
            clubQualification = listOf("자격 요건"),
            clubIntroduction = clubDescription,
            clubName = clubName,
            clubIntro = "클럽 소개",
            clubUUID = clubUUID,
            clubNotice = "공지사항",
            clubLogo = "http://example.com/logo.png", // 유효한 URL을 제공하거나 기본 URL 설정
            clubPrice = 0,
            memberCount = 0
        )

        val db = AppDatabase.getInstance(this)
        CoroutineScope(Dispatchers.IO).launch {
            db.clubDao().insertClub(club)
            runOnUiThread {
                Toast.makeText(this@CreateClubActivity, "클럽이 성공적으로 생성되었습니다", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}
