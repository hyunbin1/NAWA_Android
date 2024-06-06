package com.example.nawa

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nawa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.club1.setOnClickListener{
            val intent:Intent = Intent(this, ClubMainActivity::class.java).apply{

            }
            startActivity(intent)
        }
        binding.noticeBtn.setOnClickListener{
            val intent:Intent = Intent(this, SystemNoticeMainActivity::class.java).apply{

            }
            startActivity(intent)
        }
        binding.moreClubBtn.setOnClickListener{
            val intent:Intent = Intent(this, ClubsDetailActivity::class.java).apply{

            }
            startActivity(intent)
        }
    }
}