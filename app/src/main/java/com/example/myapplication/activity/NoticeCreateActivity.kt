package com.example.myapplication.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityCreateNoticeBinding

class NoticeCreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}