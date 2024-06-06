package com.example.myapplication.activity.signUp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.data.DTO.Request.InitSignUpRequest
import com.example.myapplication.data.DTO.Response.LoginResponse
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityJoinFirstBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InitSignUpActivity : AppCompatActivity() {
    private lateinit var initSignUpBinding: ActivityJoinFirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSignUpBinding = ActivityJoinFirstBinding.inflate(layoutInflater)
        setContentView(initSignUpBinding.root)

        initSignUpBinding.nextButton.setOnClickListener {
            val email = initSignUpBinding.email.text.toString()
            val password = initSignUpBinding.password.text.toString()
            val passwordAgain = initSignUpBinding.passwordAgain.text.toString()

            // 이메일, 패스워드가 입력된 상태로 로그인 버튼 클릭 시에 login 메서드로 넘김
            if (email.isNotEmpty() && password.isNotEmpty() && passwordAgain.isNotEmpty()) {
                if (isValidEmail(email) && isValidPassword(password) && isPasswordMatching(password, passwordAgain)) {
                    postSignUp(email, password)
                } else {
                    if (!isValidEmail(email)) {
                        Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                    } else if (!isValidPassword(password)) {
                        Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    } else if (!isPasswordMatching(password, passwordAgain)) {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length in 8..16// 비밀번호 최소 길이 예시
    }

    private fun isPasswordMatching(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    private fun postSignUp(emailId: String, password: String) {
        val initSignUpRequest = InitSignUpRequest(emailId, password, false)
        val callInitSignUp = RetrofitClient.memberAPIService.initSignUp(initSignUpRequest)

        callInitSignUp.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    // Access token 저장
                    loginResponse?.data?.let {
                        Log.d("LoginActivity", "Access Token: ${it.accessToken}")
                        saveAccessToken(it.accessToken)
                        val intent = Intent(this@InitSignUpActivity, NicknameSignUpActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    // 로그인 화면으로 이동

                    // TODO: 성공 처리 (예: 다음 화면으로 이동)
                } else {
                    // TODO: 실패 처리 (예: 에러 메시지 표시)
                    Toast.makeText(this@InitSignUpActivity, "Sign up failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@InitSignUpActivity, NicknameSignUpActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 네트워크 오류 또는 기타 오류 처리
                Toast.makeText(this@InitSignUpActivity, "Sign up failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveAccessToken(token: String) {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("ACCESS_TOKEN", token)
        editor.apply()
    }
}
