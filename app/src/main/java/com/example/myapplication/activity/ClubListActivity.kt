package com.example.myapplication.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.database.Club
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ListClubBinding
import com.example.myapplication.adapter.ClubBannerAdapter
import com.example.myapplication.data.database.toClubBannerRequest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClubListActivity : AppCompatActivity() {

    private lateinit var binding: ListClubBinding
    private lateinit var clubAdapter: ClubBannerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListClubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchAllClubs()
    }

    private fun setupRecyclerView() {
        clubAdapter = ClubBannerAdapter()
        binding.clubRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ClubListActivity)
            adapter = clubAdapter
        }
    }

    private fun fetchAllClubs() {
        // Room 데이터베이스에서 클럽 데이터를 가져옴
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            val clubsFromDb = db.clubDao().getAllClubBanners()
            clubAdapter.setClubs(clubsFromDb)

            // 클럽 이름 로그로 출력
            clubsFromDb.forEach { club ->
                Log.d("ClubListActivity", "로컬 클럽: ${club.clubName}")
            }
        }

        // API에서 클럽 데이터를 가져옴
        val call = RetrofitClient.apiService.getClubs()
        call.enqueue(object : Callback<List<Club>> {
            override fun onResponse(call: Call<List<Club>>, response: Response<List<Club>>) {
                if (response.isSuccessful) {
                    response.body()?.let { clubsFromApi ->
                        val clubBannerRequests = clubsFromApi.map { it.toClubBannerRequest() }
                        clubAdapter.addClubs(clubBannerRequests)


                        // 클럽 이름 로그로 출력
                        clubsFromApi.forEach { club ->
                            Log.d("ClubListActivity", "API 클럽: ${club.clubName}")
                        }
                    } ?: run {
                        Log.e("ClubListActivity", "클럽 응답이 없습니다.")
                    }
                } else {
                    Log.e("ClubListActivity", "클럽을 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@ClubListActivity, "클럽을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Club>>, t: Throwable) {
                Log.e("ClubListActivity", "클럽을 가져오는 중 오류가 발생했습니다: ${t.message}")
                Toast.makeText(this@ClubListActivity, "클럽을 가져오는 중 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



}
