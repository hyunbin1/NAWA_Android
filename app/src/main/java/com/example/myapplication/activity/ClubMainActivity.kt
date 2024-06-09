package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.example.myapplication.data.DTO.Response.MembershipResponse
import com.example.myapplication.data.database.Club
import com.example.myapplication.data.helper.ClubDbHelper
import com.example.myapplication.data.model.Member
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityClubMainBinding
import com.example.myapplication.databinding.DialogCompleteClubJoinRegistrationBinding
import com.example.myapplication.databinding.DialogSignupRequestBinding
import com.example.myapplication.fragment.ClubDetailFragment
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myapplication.R
import com.example.nawa.ClubReviewFragment

class ClubMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClubMainBinding
    private lateinit var clubUUID: String
    private var isSqlite: Boolean = false
    private var jwtToken: String? = null
    private var userEmail: String? = null
    private var isMember: Boolean = false
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClubMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MainActivity에서 전달된 clubUUID와 JWT 토큰, isSqlite 여부를 받아옴
        clubUUID = intent.getStringExtra("CLUB_UUID") ?: ""
        isSqlite = intent.getBooleanExtra("IS_SQLITE", false)
        jwtToken = intent.getStringExtra("JWT_TOKEN")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.clubJoinBtn.setOnClickListener {
            if (jwtToken.isNullOrEmpty()) {
                showLoginDialog()
            } else {
                showCompletionDialog(binding.clubTitle.text.toString())
            }
        }

        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        jwtToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        Log.d("ClubMainActivity", "JWT Token: $jwtToken")

        setupProfileImage()
        setupJoinButton()
        setupViewPagerAndTabs()

        // 클럽 세부 정보와 가입 여부를 확인
        fetchClubDetail()
        checkMembership()
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
                        showToast("회원 정보를 가져오는 데 실패했습니다.")
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
                showCompletionDialog(binding.clubTitle.text.toString())
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
                1 -> "후기"
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
            val callMember = RetrofitClient.apiService.getMemberInfo("Bearer $token")
            callMember.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {

                    Log.d("유저 role", response.body()?.role.toString())

                    if (response.isSuccessful) {
                        response.body()?.let { member ->
                            userEmail = member.emailId
                            isAdmin = member.role == "admin"

                            if (isAdmin) {
                                binding.clubJoinBtn.visibility = View.GONE
                            } else {
                                checkClubMembership()
                            }
                        }
                    } else {
                        showToast("회원 정보를 확인하는 데 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    showToast("회원 정보를 확인하는 중 오류가 발생했습니다.")
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
            callIsJoin.enqueue(object : Callback<MembershipResponse> {
                override fun onResponse(call: Call<MembershipResponse>, response: Response<MembershipResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { membershipResponse ->
                            isMember = membershipResponse.isMember(userEmail ?: "")
                            if (isMember) {
                                binding.clubJoinBtn.visibility = View.GONE
                            } else {
                                binding.clubJoinBtn.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        showToast("회원 정보를 확인하는 데 실패했습니다다.")
                    }
                }

                override fun onFailure(call: Call<MembershipResponse>, t: Throwable) {
                    showToast("회원 정보를 확인하는 중 오류가 발생했습니다다.")
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
            .placeholder(R.drawable.ic_launcher_background) // 기본 이미지 설정
            .error(R.drawable.ic_launcher_background) // 오류 발생 시 기본 이미지 설정
            .into(binding.clubImage)

        // 회원 수 설정
        binding.memberCount.text = club.memberCount.toString()

        // ViewPager의 Fragment에 클럽 정보 전달
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is ClubDetailFragment) {
                fragment.displayClubDetail(club)
                Log.d("ClubMainActivity", "클럽 바인딩 불러옴")
            }
        }

        // 추가적인 클럽 세부 정보 설정
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    inner class ViewPagerAdapter(activity: AppCompatActivity, private val clubUUID: String, private val isSqlite: Boolean) :
        FragmentStateAdapter(activity) {

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ClubDetailFragment.newInstance(clubUUID, isSqlite)
                1 -> ClubReviewFragment.newInstance(clubUUID)
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }
    }
}
