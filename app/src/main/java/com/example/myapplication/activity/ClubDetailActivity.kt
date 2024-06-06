
package com.example.myapplication.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.data.database.Club
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityClubDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClubDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClubDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClubDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clubUUID = intent.getStringExtra("CLUB_UUID")
        clubUUID?.let {
            fetchClubDetail(it)
        }
    }
    
    /** api 요청을 통해서 클럽의 세부 정보를 가져옴 */
    private fun fetchClubDetail(clubUUID: String) {
        val call = RetrofitClient.apiService.getClubDetail(clubUUID)
        call.enqueue(object : Callback<Club> {
            override fun onResponse(call: Call<Club>, response: Response<Club>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        displayClubDetail(it)
                    }
                } else {
                    Log.e("ClubDetailActivity", "Failed to fetch club detail: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Club>, t: Throwable) {
                Log.e("ClubDetailActivity", "Error: ${t.message}")
            }
        })
    }

    private fun displayClubDetail(club: Club) {
        binding.clubName.text = club.clubName
        binding.clubIntro.text = club.clubIntro
        binding.clubDescription.text = club.clubIntroduction
        Glide.with(binding.clubLogo.context)
            .load(club.clubLogo)
            .into(binding.clubLogo)
    }
}
