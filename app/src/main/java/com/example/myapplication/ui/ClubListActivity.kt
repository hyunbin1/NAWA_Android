package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.model.Club
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ListClubBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClubListActivity : AppCompatActivity() {

    private lateinit var binding: ListClubBinding
    private lateinit var clubAdapter: ClubAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListClubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchAllClubs()
    }

    private fun setupRecyclerView() {
        clubAdapter = ClubAdapter()
        binding.clubRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ClubListActivity)
            adapter = clubAdapter
        }
    }

    private fun fetchAllClubs() {
        val call = RetrofitClient.apiService.getClubs()
        call.enqueue(object : Callback<List<Club>> {
            override fun onResponse(call: Call<List<Club>>, response: Response<List<Club>>) {
                if (response.isSuccessful) {
                    response.body()?.let { clubs ->
                        clubAdapter.setClubs(clubs)
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
