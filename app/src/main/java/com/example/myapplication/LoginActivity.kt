package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.auth.request.LoginRequest
import com.example.myapplication.auth.response.LoginResponse
import com.example.myapplication.auth.service.RetrofitClient
import com.example.myapplication.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        val request = LoginRequest(emailId = email, password = password)
        val call = RetrofitClient.apiService.login(request)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Toast.makeText(this@LoginActivity, "Login Successful, Token: ${loginResponse?.data?.accessToken}", Toast.LENGTH_LONG).show()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG).show()
                    Log.e("LoginActivity", "Login failed with response code: ${response.code()}")
                    Log.e("LoginActivity", "Error body: $errorBody")
                }
                Toast.makeText(this@LoginActivity, "Login Successful, Token: ${response.body()?.data?.accessToken}", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("LoginActivity", "API call failed", t)
            }
        })
    }
}
