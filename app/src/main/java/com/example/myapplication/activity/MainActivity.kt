package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.example.myapplication.data.database.Notification
import com.example.myapplication.data.database.enum.NotificationCategory
import com.example.myapplication.data.database.toClubBannerRequest
import com.example.myapplication.data.helper.NoticeDbHelper
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
    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupToolbar()
        fetchClubBanners()
        fetchNotices()

        checkLoginStatus()


        binding.moreBtn.setOnClickListener {
            val intent = Intent(this, ClubListActivity::class.java)
            startActivity(intent)
        }

        binding.noticeBtn.setOnClickListener {
            val intent = Intent(this, NoticeListActivity::class.java)
            startActivity(intent)
        }

        binding.writeNoticeBtn.setOnClickListener {
            val intent = Intent(this, NoticeCreateActivity::class.java)
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
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
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
        val dbHelper = NoticeDbHelper(this)
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            NoticeDbHelper.TABLE_NAME,
            null, // 모든 열을 선택
            null, // 조건 없음
            null, // 조건 값 없음
            null, // 그룹핑하지 않음
            null, // 그룹핑 조건 값 없음
            "${NoticeDbHelper.COLUMN_CREATE_AT} DESC" // 최신 순으로 정렬
        )

        val notices = mutableListOf<Notification>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_ID))
                val noticeUUID = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_NOTICE_UUID))
                val category = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_CATEGORY))
                val title = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_TITLE))
                val content = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_CONTENT))
                val pinned = getInt(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_PINNED)) > 0
                val pinnedAt = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_PINNED_AT))
                val viewCount = getInt(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_VIEW_COUNT))
                val createAt = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_CREATE_AT))
                val updatedAt = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_UPDATED_AT))

                try {
                    val notificationCategory = NotificationCategory.valueOf(category)
                    val notice = Notification(
                        id,
                        noticeUUID,
                        notificationCategory,
                        title,
                        content,
                        pinned,
                        pinnedAt,
                        viewCount,
                        createAt,
                        updatedAt
                    )
                    notices.add(notice)
                } catch (e: IllegalArgumentException) {
                    Log.e("MainActivity", "Invalid category value: $category")
                }
            }
        }
        cursor.close()

        val limitedNotices = if (notices.size > 5) notices.take(5) else notices
        limitedNotices.forEach { notice ->
            Log.d("MainActivity", "공지사항 ID: ${notice.id}")
        }
        noticeAdapter.setNotices(limitedNotices)
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
                                binding.addClubBtn.visibility = View.VISIBLE
                                binding.writeNoticeBtn.visibility = View.VISIBLE
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

    }
}
