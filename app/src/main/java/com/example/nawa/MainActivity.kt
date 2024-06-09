package com.example.nawa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.activity.ClubMainActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.setOnClickListener{
            val intent:Intent = Intent(this, ClubMainActivity::class.java).apply{

            }
            startActivity(intent)
        }
        binding.noticeBtn.setOnClickListener{
            val intent:Intent = Intent(this, SystemNoticeMainActivity::class.java).apply{

            }
            startActivity(intent)
        }
        binding.moreBtn.setOnClickListener{
            val intent:Intent = Intent(this, ClubsDetailActivity::class.java).apply{

            }
            startActivity(intent)
        }
    }
}