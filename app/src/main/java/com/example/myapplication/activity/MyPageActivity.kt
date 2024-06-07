package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.database.Club
import com.example.myapplication.data.model.Member
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityMypageBinding
import com.example.myapplication.adapter.MyPageClubAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    private lateinit var myprofileButton: Button
    private lateinit var clubAdapter: MyPageClubAdapter
    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myprofileButton = binding.myprofileButton

        setupRecyclerView()
        fetchMemberInfo()
        fetchClubs()

        myprofileButton.setOnClickListener {
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
        }

        binding.logoutButton.setOnClickListener {
            logout()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        clubAdapter = MyPageClubAdapter()
        binding.clubRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyPageActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = clubAdapter
        }
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
                            binding.username.text = "${member.nickname}님"
                            Glide.with(this@MyPageActivity)
                                .load(member.profileImage)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(binding.userImage)
                        } ?: run {
                            Log.e("MyPageActivity", "회원 정보 응답이 없습니다.")
                        }
                    } else {
                        Log.e("MyPageActivity", "회원 정보를 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    Log.e("MyPageActivity", "회원 정보를 가져오는 중 오류가 발생했습니다: ${t.message}")
                }
            })
        }
    }

    private fun fetchClubs() {
        val call = RetrofitClient.apiService.getClubs()
        call.enqueue(object : Callback<List<Club>> {
            override fun onResponse(call: Call<List<Club>>, response: Response<List<Club>>) {
                if (response.isSuccessful) {
                    response.body()?.let { clubs ->
                        clubAdapter.setClubs(clubs)
                    } ?: run {
                        Log.e("MyPageActivity", "클럽 응답이 없습니다.")
                    }
                } else {
                    Log.e("MyPageActivity", "클럽을 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Club>>, t: Throwable) {
                Log.e("MyPageActivity", "클럽을 가져오는 중 오류가 발생했습니다: ${t.message}")
            }
        })
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("ACCESS_TOKEN")
        editor.apply()

        isLoggedIn = false
    }
}
