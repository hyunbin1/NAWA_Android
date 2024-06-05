package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.model.Club
import com.example.myapplication.data.model.Member
import com.example.myapplication.data.model.Notice
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var clubAdapter: ClubAdapter
    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var loginButton: Button
    
    // 로그인 여부 확인
    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginButton = binding.loginButton

        setupRecyclerView()
        setupToolbar()
        fetchClubs()
        fetchNotices()

        checkLoginStatus()

        /** 로그인 상태에서 버튼 클릭시 로그아웃
         * 로그인을 안했을 경우 activity_login 페이지로 넘김(액티비티는 LoginActivity 로 넘김) */
        loginButton.setOnClickListener {
            if (isLoggedIn) {
                logout()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        /** 클럽 더보기 버튼 클릭시 리스너
         *  list_club 페이지로 넘김(액티비티는 ClubListActivity로 넘김) */
        binding.moreBtn.setOnClickListener {
            val intent = Intent(this, ClubListActivity::class.java)
            startActivity(intent)
        }

        /** 공지사항 더보기 버튼 클릭시 리스너
         *  list_notice 페이지로 넘김(액티비티는 NoticeListActivity 로 넘김) */
        binding.noticeBtn.setOnClickListener {
            val intent = Intent(this, NoticeListActivity::class.java)
            startActivity(intent)
        }

        if (isLoggedIn) {
            fetchMemberInfo()
        }
    }

    /** toolbar.xml 세팅 */
    private fun setupToolbar() {
        setSupportActionBar(binding.mainToolBar.mainToolBar)
    }

    /** menu_toolbar.xml 세팅, toolbar 에서 (마이페이지를)볼 수 있음 */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    /** toolbar 의 마이페이지 클릭시 작동
     * 로그인을 한 경우는 activity_myprofile 로 이동(액티비티는 MyProfileActivity 로 넘김)
     * 로그인을 안했을 경우에는 activity_notice_dialog 를 띄워 로그인창으로 유도 */
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

    /** activity_notice_dialog 를 띄움 */
    private fun showLoginDialog() {
        val loginDialogFragment = LoginDialogFragment()
        loginDialogFragment.show(supportFragmentManager, "LoginDialogFragment")
    }

    /** 클럽 리스트, 공지사항 리스트 리사이클 뷰를 띄움 */
    private fun setupRecyclerView() {
        clubAdapter = ClubAdapter()
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

    /** api 요청을 통해 클럽 리스트에서 5개의 클럽만 불러옴 */
    private fun fetchClubs() {
        val call = RetrofitClient.apiService.getClubs()
        call.enqueue(object : Callback<List<Club>> {
            override fun onResponse(call: Call<List<Club>>, response: Response<List<Club>>) {
                if (response.isSuccessful) {
                    response.body()?.let { clubs ->
                        val limitedClubs = if (clubs.size > 5) clubs.take(5) else clubs

                        // 로그 출력
                        limitedClubs.forEach { club ->
                            Log.d("MainActivity", "클럽 이름: ${club.clubName}")
                        }

                        clubAdapter.setClubs(limitedClubs)
                    } ?: run {
                        Log.e("MainActivity", "클럽 응답이 없습니다.")
                    }
                } else {
                    Log.e("MainActivity", "클럽을 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                    Log.e("MainActivity", "오류 내용: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Club>>, t: Throwable) {
                Log.e("MainActivity", "클럽을 가져오는 중 오류가 발생했습니다: ${t.message}")
            }
        })
    }

    /** api 요청을 통해 클럽 리스트에서 5개의 공지사항만 불러옴 */
    private fun fetchNotices() {
        val call = RetrofitClient.apiService.getNotices()
        call.enqueue(object : Callback<List<Notice>> {
            override fun onResponse(call: Call<List<Notice>>, response: Response<List<Notice>>) {
                if (response.isSuccessful) {
                    response.body()?.let { notices ->
                        val limitedNotices = if (notices.size > 5) notices.take(5) else notices

                        // 로그 출력
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

    /** 로그인을 하여 토큰이 있을 경우 유저 정보를 가져옴 */
    private fun fetchMemberInfo() {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        accessToken?.let { token ->
            val call = RetrofitClient.apiService.getMemberInfo("Bearer $token")
            call.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        response.body()?.let { member ->
                            Log.d("MainActivity", "회원 정보: ${member.nickname}, 이메일: ${member.emailId}")
                            // 회원 정보를 UI에 표시하거나 처리하는 코드 추가
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

    /** 토큰 유무를 통해서 로그인 상태를 체크함 */
    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        isLoggedIn = !accessToken.isNullOrEmpty()

        updateLoginButtonText()
    }

    private fun updateLoginButtonText() {
        loginButton.text = if (isLoggedIn) "로그아웃" else "로그인(임시)"
    }

    /** 토큰을 제거하여 로그아웃 */
    private fun logout() {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("ACCESS_TOKEN")
        editor.apply()

        isLoggedIn = false
        updateLoginButtonText()
    }
}
