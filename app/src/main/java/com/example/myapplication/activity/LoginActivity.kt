package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.data.DTO.Request.InitSignUpRequest
import com.example.myapplication.activity.signUp.InitSignUpActivity
import com.example.myapplication.data.DTO.Request.LoginRequest
import com.example.myapplication.data.DTO.Response.LoginResponse
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityLoginBinding
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // 이메일, 패스워드가 입력된 상태로 로그인 버튼 클릭 시에 login 메서드로 넘김
            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
        binding.kakaologinButton.setOnClickListener {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Toast.makeText(this, "로그인 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                } else if (token != null) {
                    // 로그인 성공 시 처리
                    fetchKakaoUserInfo(token.accessToken)
                }
            }
        }
        binding.registration.setOnClickListener {
            val intent = Intent(this, InitSignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchKakaoUserInfo(kakaoToken: String) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Toast.makeText(this, "사용자 정보 요청 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            } else if (user != null) {
                // 사용자 정보 가져오기
                val id = user.id.toString()
                val nickname = user.kakaoAccount?.profile?.nickname ?: ""
                val email = user.kakaoAccount?.email ?: ""
                val memberUUID = java.util.UUID.randomUUID().toString()

                // 회원가입 요청
                val signUpRequest = InitSignUpRequest(email, "", true)
                val call = RetrofitClient.memberAPIService.initSignUp(signUpRequest)

                call.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            // 회원가입 성공 시 로그인 요청
                            login(email, "")
                        } else {
                            Toast.makeText(this@LoginActivity, "회원가입 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "회원가입 요청 중 오류 발생: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        binding.registration.setOnClickListener{
            val intent = Intent(this, InitSignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /** edit text에 작성된 email, password를 로그인 외부 api를 이용하여 post 요청 후
     * 로그인 성공시에 토큰을 발급받고 메인화면으로 이동 */
    private fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        // 이곳에서 api 호출
        val call = RetrofitClient.apiService.login(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.data?.let {
                        // 로그인 성공 처리
                        Log.d("LoginActivity", "Access Token: ${it.accessToken}")
                        saveAccessToken(it.accessToken)
                        // 메인 액티비티로 이동
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.e("LoginActivity", "Login failed: ${response.code()} - ${response.message()}")
                    Log.e("LoginActivity", "Error body: ${response.errorBody()?.string()}")
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Login error: ${t.message}")
                Toast.makeText(this@LoginActivity, "Login error: ${t.message}", Toast.LENGTH_SHORT).show()
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
