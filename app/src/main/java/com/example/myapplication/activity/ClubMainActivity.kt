package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.adapter.ViewPagerAdapter
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

class ClubMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClubMainBinding
    private lateinit var clubUUID: String
    private var isSqlite: Boolean = false
    private var jwtToken: String? = null
    private var userEmail: String? = null
    private var isMember: Boolean = false
    private var isAdmin: Boolean = false
    private var googleFormLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClubMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MainActivity에서 전달된 clubUUID와 JWT 토큰을 받아옴
        clubUUID = intent.getStringExtra("CLUB_UUID") ?: ""
        isSqlite = intent.getBooleanExtra("IS_SQLITE", false)

        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        jwtToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        Log.d("ClubMainActivity", "JWT Token: $jwtToken")

        // 클럽 세부 정보와 가입 여부를 확인
        fetchClubDetail()
        checkMembership()
        setupProfileImage()
        setupJoinButton()
        setupViewPagerAndTabs()
    }

    private fun setupProfileImage() {
        val profileImageView = binding.profileImage
        jwtToken?.let { token ->
            val call = RetrofitClient.apiService.getMemberInfo("Bearer $token")
            call.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        response.body()?.let { member ->
                            val profileImage = member.profileImage
                            Glide.with(this@ClubMainActivity)
                                .load(profileImage)
                                .circleCrop()
                                .into(profileImageView)
                        }
                    } else {
                        showToast("회원 프로필 정보를 가져오는 데 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    showToast("회원 정보를 가져오는 중 오류가 발생했습니다.")
                }
            })
        }

        profileImageView.setOnClickListener {
            handleMyPageClick()
        }
    }

    private fun setupJoinButton() {
        binding.clubJoinBtn.setOnClickListener {
            if (jwtToken.isNullOrEmpty()) {
                // 로그인하지 않은 경우 로그인 다이얼로그를 보여줌
                showLoginDialog()
            } else if (isMember) {
                // 이미 클럽에 가입한 경우
                showToast("이미 클럽에 가입되어 있습니다.")
            } else {
                // 클럽에 가입되지 않은 경우
                googleFormLink?.let { link ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(intent)
                } ?: showToast("가입 신청 링크를 불러오는 데 실패했습니다.")
                Log.d("ClubMainActivity", "Google Form Link: $googleFormLink")
            }
        }
    }

    private fun setupViewPagerAndTabs() {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(this, clubUUID, isSqlite)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "클럽 소개"
                else -> null
            }
        }.attach()
    }

    private fun handleMyPageClick() {
        if (jwtToken.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }

    // 클럽 가입 신청 완료
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

    // 클럽 세부 정보를 API를 통해 가져옴
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
                        Log.d("ClubMainActivity", "Club Name: ${it.clubName}")
                        Log.d("ClubMainActivity", "Club Introduction: ${it.clubIntroduction}")
                        Log.d("ClubMainActivity", "Club Logo: ${it.clubLogo}")
                        Log.d("ClubMainActivity", "Member Count: ${it.memberCount}")
                        Log.d("ClubMainActivity", "Google Form Link: ${it.googleForm}")
                        displayClubDetail(it)
                        googleFormLink = it.googleForm // Google Forms 링크 저장
                        Log.d("ClubMainActivity", "Google Form Link: $googleFormLink")
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
            val callMember = RetrofitClient.apiService.getMemberInfo("Bearer $token")
            callMember.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    Log.d("ClubMainActivity", "Response Code: ${response.code()}")
                    Log.d("ClubMainActivity", "Response Message: ${response.message()}")

                    if (response.isSuccessful) {
                        val member = response.body()
                        if (member != null) {
                            Log.d("ClubMainActivity", "User Email: ${member.emailId}")
                            Log.d("ClubMainActivity", "User Role: ${member.role}")

                            userEmail = member.emailId
                            isAdmin = member.role == "admin"

                            Log.d("ClubMainActivity", "Is Admin: $isAdmin")

                            if (isAdmin) {
                                binding.clubJoinBtn.visibility = View.GONE
                            } else {
                                checkClubMembership()
                            }
                        } else {
                            Log.e("ClubMainActivity", "회원 정보가 null입니다.")
                            showToast("회원 정보를 확인하는 데 실패했습니다.")
                        }
                    } else {
                        Log.e("ClubMainActivity", "응답 실패: ${response.code()} ${response.message()}")
                        showToast("회원 정보를 확인하는 데 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    Log.e("ClubMainActivity", "네트워크12 오류: ${t.message}")
                    showToast("회원 정보를 확인하는는 중 오류가 발생했습니다.")
                }
            })
        } ?: run {
            // 비회원인 경우 가입하기 버튼 보이기
            binding.clubJoinBtn.visibility = View.VISIBLE
        }
    }

    private fun checkClubMembership() {
        jwtToken?.let { token ->
            val callIsJoin = RetrofitClient.apiService.checkClubMembership("Bearer $token", clubUUID)
            callIsJoin.enqueue(object : Callback<List<MembershipResponse>> {
                override fun onResponse(call: Call<List<MembershipResponse>>, response: Response<List<MembershipResponse>>) {
                    Log.d("ClubMainActivity", "Response Code: ${response.code()}")
                    Log.d("ClubMainActivity", "Response Message: ${response.message()}")

                    if (response.isSuccessful) {
                        val membershipResponseList = response.body()
                        if (membershipResponseList != null && membershipResponseList.isNotEmpty()) {
                            // 이메일을 비교하여 해당 사용자가 회원인지 확인
                            isMember = membershipResponseList.any { it.isMember(userEmail ?: "") }
                            Log.d("ClubMainActivity", "Is Member: $isMember")

                            if (isMember) {
                                binding.clubJoinBtn.visibility = View.GONE
                            } else {
                                binding.clubJoinBtn.visibility = View.VISIBLE
                            }
                        } else {
                            Log.e("ClubMainActivity", "회원 응답 정보가 null이거나 빈 배열입니다.")
                            showToast("회원 정보를 확인하는 데 실패했습니다.")
                        }
                    } else {
                        Log.e("ClubMainActivity", "응답 실패: ${response.code()} ${response.message()}")
                        showToast("회원 정보를 확인하는 데 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<List<MembershipResponse>>, t: Throwable) {
                    Log.e("ClubMainActivity", "네트워크 오류: ${t.message}")
                    showToast("회원 정보를 확인하는 중 오류가 발생했습니다.")
                }
            })
        }
    }

    private fun displayClubDetail(club: Club) {
        binding.clubBannerTitle.text = club.clubName // 클럽 배너 제목
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
