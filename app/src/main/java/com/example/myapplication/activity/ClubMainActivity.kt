package com.example.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.myapplication.data.DTO.Response.MembershipResponse
import com.example.myapplication.data.database.Club
import com.example.myapplication.data.helper.ClubDbHelper
import com.example.myapplication.data.model.Member
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityClubMainBinding
import com.example.myapplication.databinding.DialogCompleteClubJoinRegistrationBinding
import com.example.myapplication.databinding.DialogSignupRequestBinding
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.Menu
import android.widget.ImageView
import com.example.myapplication.R

class ClubMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClubMainBinding
    private lateinit var clubUUID: String
    private var isSqlite: Boolean = false
    private var jwtToken: String? = null
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClubMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MainActivity에서 전달된 clubUUID와 JWT 토큰, isSqlite 여부를 받아옴
        clubUUID = intent.getStringExtra("CLUB_UUID") ?: ""
        isSqlite = intent.getBooleanExtra("IS_SQLITE", false)
        jwtToken = intent.getStringExtra("JWT_TOKEN")

        val toolbar: Toolbar = binding.clubMainToolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.clubJoinBtn.setOnClickListener {
            if (jwtToken.isNullOrEmpty()) {
                showLoginDialog()
            } else {
                showCompletionDialog(binding.clubTitle.text.toString())
            }
        }

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "클럽 소개"
                1 -> "후기"
                else -> null
            }
        }.attach()

        // 클럽 세부 정보와 가입 여부를 확인
        fetchClubDetail()
        getUserInfoAndCheckMembership()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        jwtToken?.let { token ->
            val call = RetrofitClient.apiService.getMemberInfo("Bearer $token")
            call.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        response.body()?.let { member ->
                            val profileImage = member.profileImage
                            val menuItem = menu?.findItem(R.id.myPage)
                            Glide.with(this@ClubMainActivity)
                                .load(profileImage)
                                .circleCrop()
                                .into(menuItem?.actionView as ImageView)
                        }
                    } else {
                        showToast("회원 정보를 가져오는 데 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    showToast("회원 정보를 가져오는 중 오류가 발생했습니다.")
                }
            })
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //클럽 가입 신청 완료
    private fun showCompletionDialog(clubTitle: String) {
        val dialogBinding = DialogCompleteClubJoinRegistrationBinding.inflate(LayoutInflater.from(this))

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        dialogBinding.clubTitle.text = clubTitle // 클럽 제목을 설정
        dialogBinding.completeBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    // 로그인 다이어로그
    private fun showLoginDialog() {
        val dialogBinding = DialogSignupRequestBinding.inflate(LayoutInflater.from(this))

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        dialogBinding.goToJoin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            alertDialog.dismiss()
        }

        dialogBinding.cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun fetchClubDetail() {
        if (isSqlite) {
            fetchClubDetailFromDb()
        } else {
            fetchClubDetailFromApi()
        }
    }

    private fun fetchClubDetailFromDb() {
        val dbHelper = ClubDbHelper(this)
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            ClubDbHelper.TABLE_NAME,
            arrayOf(
                ClubDbHelper.COLUMN_CLUB_UUID,
                ClubDbHelper.COLUMN_CLUB_NAME,
                ClubDbHelper.COLUMN_CLUB_INTRO,
                ClubDbHelper.COLUMN_CLUB_LOGO,
                ClubDbHelper.COLUMN_CLUB_INTRODUCTION,
                ClubDbHelper.COLUMN_CLUB_QUALIFICATION,
                ClubDbHelper.COLUMN_CLUB_REGIS_PROCESS,
                ClubDbHelper.COLUMN_CLUB_NOTICE,
                ClubDbHelper.COLUMN_CLUB_CANCEL_INTRODUCTION,
                ClubDbHelper.COLUMN_CLUB_PRICE,
                ClubDbHelper.COLUMN_MEMBER_COUNT,
                ClubDbHelper.COLUMN_CREATE_AT,
                ClubDbHelper.COLUMN_UPDATED_AT
            ),
            "${ClubDbHelper.COLUMN_CLUB_UUID} = ?",
            arrayOf(clubUUID),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val clubUUID = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_UUID))
            val clubName = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_NAME))
            val clubIntro = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_INTRO))
            val clubLogo = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_LOGO))
            val clubIntroduction = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_INTRODUCTION))
            val clubQualification = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_QUALIFICATION))?.split(",")
            val clubRegisProcess = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_REGIS_PROCESS))
            val clubNotice = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_NOTICE))
            val clubCancelIntroduction = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_CANCEL_INTRODUCTION))?.split(",")
            val clubPrice = cursor.getInt(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_PRICE))
            val memberCount = cursor.getInt(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_MEMBER_COUNT))

            val club = Club(
                id = 0,
                clubUUID = clubUUID,
                clubName = clubName,
                clubIntro = clubIntro,
                clubLogo = clubLogo,
                clubIntroduction = clubIntroduction,
                clubQualification = clubQualification,
                clubRegisProcess = clubRegisProcess,
                clubNotice = clubNotice,
                clubCancelIntroduction = clubCancelIntroduction,
                clubPrice = clubPrice,
                memberCount = memberCount
            )
            displayClubDetail(club)
        }
        cursor.close()
    }

    private fun fetchClubDetailFromApi() {
        val call = RetrofitClient.apiService.getClubDetail(clubUUID)
        call.enqueue(object : Callback<Club> {
            override fun onResponse(call: Call<Club>, response: Response<Club>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        displayClubDetail(it)
                    }
                } else {
                    showToast("클럽 세부 정보를 가져오는 데 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<Club>, t: Throwable) {
                showToast("클럽 세부 정보를 가져오는 중 오류가 발생했습니다.")
            }
        })
    }

    // 클럽 가입 여부 확인(신청 버튼)
    private fun checkMembership() {
        jwtToken?.let { token ->
            val call = RetrofitClient.apiService.checkClubMembership("Bearer $token", clubUUID)
            call.enqueue(object : Callback<MembershipResponse> {
                override fun onResponse(call: Call<MembershipResponse>, response: Response<MembershipResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it.data.any { member -> member.emailId == userEmail }) { // 로그인 회원과 클럽 join 회원 비교
                                binding.clubJoinBtn.visibility = View.GONE
                            } else {
                                binding.clubJoinBtn.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        showToast("회원 정보를 확인하는 데 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<MembershipResponse>, t: Throwable) {
                    showToast("회원 정보를 확인하는 중 오류가 발생했습니다.")
                }
            })
        } ?: run {
            // 비회원인 경우 가입하기 버튼 보이기
            binding.clubJoinBtn.visibility = View.VISIBLE
        }
    }

    private fun getUserInfoAndCheckMembership() {
        jwtToken?.let { token ->
            val call = RetrofitClient.apiService.getMemberInfo("Bearer $token")
            call.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        response.body()?.let { member ->
                            userEmail = member.emailId
                            checkMembership()
                        }
                    } else {
                        showToast("회원 정보를 가져오는데 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    showToast("회원 정보를 가져오는 중 오류가 발생했습니다.")
                }
            })
        } ?: run {
            // 비회원인 경우 가입하기 버튼 보이기
            binding.clubJoinBtn.visibility = View.VISIBLE
        }
    }

    private fun displayClubDetail(club: Club) {
        binding.clubTitle.text = club.clubName
        binding.clubIntroduce.text = club.clubIntroduction // 클럽 한줄 소개
        // 클럽 이미지 설정
        Glide.with(this)
            .load(club.clubLogo)
            .into(binding.clubImage)

        // 회원 수 설정
        binding.memberCount.text = club.memberCount.toString()

        // 추가적인 클럽 세부 정보 설정
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
