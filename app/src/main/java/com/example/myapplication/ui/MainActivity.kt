package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.model.Club
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var clubAdapter: ClubAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        fetchClubs()
    }

    private fun setupRecyclerView() {
        clubAdapter = ClubAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = clubAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun fetchClubs() {
        val call = RetrofitClient.apiService.getClubs()
        call.enqueue(object : Callback<List<Club>> {
            override fun onResponse(call: Call<List<Club>>, response: Response<List<Club>>) {
                if (response.isSuccessful) {
                    response.body()?.let { clubs ->
                        val limitedClubs = if (clubs.size > 5) clubs.take(5) else clubs

                        // 로그 출력
                        limitedClubs.forEach { club ->
                            Log.d("MainActivity", "Club Name: ${club.clubName}")
                        }

                        clubAdapter.setClubs(limitedClubs)
                    }
                } else {
                    Log.e("MainActivity", "Failed to fetch clubs: ${response.code()} - ${response.message()}")
                    Log.e("MainActivity", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Club>>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
            }
        })
    }
}
