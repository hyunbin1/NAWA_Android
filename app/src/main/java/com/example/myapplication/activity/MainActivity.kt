package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.model.Member
import com.example.myapplication.data.model.Notice
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.adapter.ClubBannerAdapter
import com.example.myapplication.adapter.LoginDialogFragment
import com.example.myapplication.adapter.NoticeAdapter
import com.example.myapplication.data.DTO.Request.ClubBannerDTO
import com.example.myapplication.data.database.toClubBannerRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var clubAdapter: ClubBannerAdapter
    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var loginButton: Button
    private lateinit var createClubButton: Button
    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginButton = binding.loginButton
        createClubButton = binding.createClubButton

        setupRecyclerView()
        setupToolbar()
        fetchClubBanners()
        fetchNotices()

        checkLoginStatus()

        loginButton.setOnClickListener {
            if (isLoggedIn) {
                logout()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        binding.moreBtn.setOnClickListener {
            val intent = Intent(this, ClubListActivity::class.java)
            startActivity(intent)
        }

        binding.noticeBtn.setOnClickListener {
            val intent = Intent(this, NoticeListActivity::class.java)
            startActivity(intent)
        }

        createClubButton.setOnClickListener {
            val intent = Intent(this, CreateClubActivity::class.java)
            startActivity(intent)
        }

        if (isLoggedIn) {
            fetchMemberInfo()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.mainToolBar.mainToolBar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.myPage -> {
                if (isLoggedIn) {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                } else {
                    showLoginDialog()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoginDialog() {
        val loginDialogFragment = LoginDialogFragment()
        loginDialogFragment.show(supportFragmentManager, "LoginDialogFragment")
    }

    private fun setupRecyclerView() {
        clubAdapter = ClubBannerAdapter()
        noticeAdapter = NoticeAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = clubAdapter
            isNestedScrollingEnabled = false
        }
        binding.noticeRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = noticeAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun fetchClubBanners() {
        val db = AppDatabase.getInstance(this)
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        val call = RetrofitClient.apiService.getClubBanners()
        call.enqueue(object : Callback<List<ClubBannerDTO>> {
            override fun onResponse(call: Call<List<ClubBannerDTO>>, response: Response<List<ClubBannerDTO>>) {
                if (response.isSuccessful) {
                    response.body()?.let { clubsFromApi ->
                        val limitedClubsFromApi = clubsFromApi.take(5)
                        clubAdapter.setClubs(limitedClubsFromApi)

                        if (limitedClubsFromApi.size < 5) {
                            coroutineScope.launch {
                                val clubsFromDb = db.clubDao().getAllClubs()
                                val neededClubs = 5 - limitedClubsFromApi.size
                                val additionalClubs = clubsFromDb.take(neededClubs).map { it.toClubBannerRequest() }
                                clubAdapter.addClubs(additionalClubs)
                            }
                        }
                    } ?: run {
                        Log.e("MainActivity", "클럽 응답이 없습니다.")
                    }
                } else {
                    Log.e("MainActivity", "클럽을 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                    Log.e("MainActivity", "오류 내용: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<ClubBannerDTO>>, t: Throwable) {
                Log.e("MainActivity", "클럽을 가져오는 중 오류가 발생했습니다: ${t.message}")
            }
        })
    }

    private fun fetchNotices() {
        val call = RetrofitClient.apiService.getNotices()
        call.enqueue(object : Callback<List<Notice>> {
            override fun onResponse(call: Call<List<Notice>>, response: Response<List<Notice>>) {
                if (response.isSuccessful) {
                    response.body()?.let { notices ->
                        val limitedNotices = if (notices.size > 5) notices.take(5) else notices

                        limitedNotices.forEach { notice ->
                            Log.d("MainActivity", "공지사항 ID: ${notice.id}")
                        }

                        noticeAdapter.setNotices(limitedNotices)
                    } ?: run {
                        Log.e("MainActivity", "공지사항 응답이 없습니다.")
                    }
                } else {
                    Log.e("MainActivity", "공지사항을 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@MainActivity, "공지사항을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Notice>>, t: Throwable) {
                Log.e("MainActivity", "공지사항을 가져오는 중 오류가 발생했습니다: ${t.message}")
                Toast.makeText(this@MainActivity, "공지사항을 가져오는 중 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
                            Log.d("MainActivity", "회원 정보: ${member.nickname}, 이메일: ${member.emailId}, 회원 상태: ${member.role}")
                            if (member.role == "admin") {
                                binding.createClubButton.visibility = View.VISIBLE
                            }
                        } ?: run {
                            Log.e("MainActivity", "회원 정보 응답이 없습니다.")
                        }
                    } else {
                        Log.e("MainActivity", "회원 정보를 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                        Toast.makeText(this@MainActivity, "회원 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    Log.e("MainActivity", "회원 정보를 가져오는 중 오류가 발생했습니다: ${t.message}")
                    Toast.makeText(this@MainActivity, "회원 정보를 가져오는 중 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        isLoggedIn = !accessToken.isNullOrEmpty()

        updateLoginButtonText()
    }

    private fun updateLoginButtonText() {
        loginButton.text = if (isLoggedIn) "로그아웃" else "로그인(임시)"
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("ACCESS_TOKEN")
        editor.apply()

        isLoggedIn = false
        updateLoginButtonText()
    }
}
