package com.example.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({
            // 일정 시간 이후 MainActivity로 이동
            val intent = Intent(this, EntranceActivity::class.java)
            startActivity(intent)

            // 이전 키 눌렀을 때 splash layout이 뜨지 않도록 방지
            finish()
        }, 2000)    // 시간 2초 이후 실행
    }
}