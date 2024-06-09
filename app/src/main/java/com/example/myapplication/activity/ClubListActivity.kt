package com.example.myapplication.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.database.Club
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ListClubBinding
import com.example.myapplication.adapter.ClubBannerAdapter
import com.example.myapplication.data.DTO.Request.ClubBannerDTO
import com.example.myapplication.data.database.toClubBannerRequest
import com.example.myapplication.data.helper.ClubDbHelper
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
        val dbHelper = ClubDbHelper(this)
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            ClubDbHelper.TABLE_NAME,
            arrayOf(
                ClubDbHelper.COLUMN_CLUB_UUID,
                ClubDbHelper.COLUMN_CLUB_NAME,
                ClubDbHelper.COLUMN_CLUB_LOGO
            ),
            null, null, null, null, null
        )

        val clubsFromDb = mutableListOf<ClubBannerDTO>()
        while (cursor.moveToNext()) {
            val clubUUID = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_UUID))
            val clubName = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_NAME))
            val clubLogo = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_LOGO))
            clubsFromDb.add(ClubBannerDTO(clubUUID, clubName, clubLogo))
        }
        cursor.close()

        if (clubsFromDb.isNotEmpty()) {
            clubAdapter.addClubs(clubsFromDb)
            clubsFromDb.forEach { club ->
                Log.d("ClubListActivity", "SQLite 클럽: ${club.clubName}")
            }
        }

        fetchClubsFromApi()
    }

    private fun fetchClubsFromApi() {
        val call = RetrofitClient.apiService.getClubs()
        call.enqueue(object : Callback<List<Club>> {
            override fun onResponse(call: Call<List<Club>>, response: Response<List<Club>>) {
                if (response.isSuccessful) {
                    response.body()?.let { clubsFromApi ->
                        val clubBannerRequests = clubsFromApi.map { it.toClubBannerRequest() }
                        clubAdapter.addClubs(clubBannerRequests)

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
