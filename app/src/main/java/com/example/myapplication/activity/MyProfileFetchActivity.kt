package com.example.myapplication.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.model.Member
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityMyprofileFetchBinding
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProfileFetchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyprofileFetchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyprofileFetchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupTabLayout()
        fetchMemberInfo()
    }

    private fun setupViewPager() {
        val adapter = MyProfileFetchPagerAdapter(this)
        binding.viewpager.adapter = adapter
    }

    private fun setupTabLayout() {
        val tabTitles = arrayOf(getString(R.string.basicinfo), getString(R.string.introduce))
        TabLayoutMediator(binding.tab, binding.viewpager) { tab, position ->
            tab.text = tabTitles[position]
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
                            binding.username.setText("${member.nickname}님")
                            Glide.with(this@MyProfileFetchActivity)
                                .load(member.profileImage)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(binding.userImage)
                            // 다른 사용자 정보 설정
                        } ?: run {
                            Log.e("MyProfileActivity", "회원 정보 응답이 없습니다.")
                        }
                    } else {
                        Log.e("MyProfileActivity", "회원 정보를 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                        Toast.makeText(this@MyProfileFetchActivity, "회원 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    Log.e("MyProfileActivity", "회원 정보를 가져오는 중 오류가 발생했습니다: ${t.message}")
                    Toast.makeText(this@MyProfileFetchActivity, "회원 정보를 가져오는 중 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
