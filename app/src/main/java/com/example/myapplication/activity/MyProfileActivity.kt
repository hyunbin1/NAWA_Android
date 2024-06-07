package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.model.Member
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityMyprofileBinding
import com.example.myapplication.adapter.MyProfilePagerAdapter
import com.example.myapplication.ui.MyProfileFetchActivity
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyprofileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPagerWithTabs()
        fetchMemberInfo()

        // 프로필 수정 버튼 클릭 시 MyProfileFetchActivity로 이동
        binding.fetchProfileButton.setOnClickListener {
            val intent = Intent(this, MyProfileFetchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupViewPagerWithTabs() {
        val adapter = MyProfilePagerAdapter(this)
        val viewPager = binding.viewpager
        val tabLayout = binding.tab

        // Set up ViewPager2 adapter
        viewPager.adapter = adapter

        // Set up TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "기본 정보"
                1 -> tab.text = "소개"
            }
        }.attach()
    }

    private fun fetchMemberInfo() {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        accessToken?.let { token ->
            val call = RetrofitClient.apiService.getMemberInfo("Bearer $token")
            call.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        response.body()?.let { member ->
                            binding.userNickname.text = "${member.nickname}님"
                            Glide.with(this@MyProfileActivity)
                                .load(member.profileImage)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(binding.userImage)
                            // 다른 사용자 정보 설정
                        } ?: run {
                            Log.e("MyProfileActivity", "회원 정보 응답이 없습니다.")
                        }
                    } else {
                        Log.e("MyProfileActivity", "회원 정보를 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                        Toast.makeText(this@MyProfileActivity, "회원 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    Log.e("MyProfileActivity", "회원 정보를 가져오는 중 오류가 발생했습니다: ${t.message}")
                    Toast.makeText(this@MyProfileActivity, "회원 정보를 가져오는 중 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
