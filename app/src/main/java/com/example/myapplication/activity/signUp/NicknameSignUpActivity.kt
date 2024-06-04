package com.example.myapplication.activity.signUp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityJoinFirstBinding
import com.example.myapplication.databinding.ActivityJoinSecondBinding

class NicknameSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinSecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinSecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitButton.setOnClickListener {


        }


    }
}