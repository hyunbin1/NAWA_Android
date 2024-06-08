package com.example.myapplication.activity.signUp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.data.DTO.Request.InitSignUpRequest
import com.example.myapplication.data.DTO.Request.MemberUpdateRequest
import com.example.myapplication.data.DTO.Response.LoginResponse
import com.example.myapplication.data.DTO.Response.MemberResponse
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityJoinFirstBinding
import com.example.myapplication.databinding.ActivityJoinSecondBinding
import com.example.myapplication.databinding.DialogJoinFinalBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class NicknameSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinSecondBinding

    private var isNicknameValid = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinSecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }


        private fun setupListeners() {
            binding.nickname.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    validateNickname(binding.nickname.text.toString())
                    checkAllInputs()
                }
            }
            binding.nickname.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    validateNickname(s.toString())
                    changeEditTextColorWithValidateNickname(s.toString())
                    checkAllInputs()
                }

                override fun afterTextChanged(s: Editable?) {}
            })


            binding.submitButton.setOnClickListener {
                val userNickname = binding.nickname.text.toString()

                if (userNickname.isNotEmpty()) { // 모든 텍스트에 입력 완료
                    if (isValidNickname(userNickname)) {
                        signUpComplete(userNickname)
                    } else {
                        when {
                            !isValidNickname(userNickname) -> {
                                Toast.makeText(this, "닉네임 형식에 맞추어 작성 부탁드려요 ><", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                    }
                } else {
                    Toast.makeText(this, "닉네임을 작성해주세요 :)", Toast.LENGTH_SHORT).show()
                }
            }

        }


    private fun isValidNickname(nickname: String): Boolean {
        val isValidLength = nickname.length in 2..12
        val hasRequiredCharacters = Pattern.compile("^[가-힣a-zA-Z]+$").matcher(nickname).find()
        return isValidLength && hasRequiredCharacters
    }

    private fun validateNickname(nickname: String) {
        val isValidLength = nickname.length in 2..12
        val hasRequiredCharacters = Pattern.compile("^[가-힣a-zA-Z]+$").matcher(nickname).find()
        binding.nicknameErr.text = when {
            !isValidLength -> "닉네임의 글자 수는 2자 이상 12자 이하여야 합니다."
            !hasRequiredCharacters -> "닉네임은 한글과 영문만 입력 가능합니다."
            else -> ""
        }
        binding.nicknameErr.visibility = if (binding.nicknameErr.text.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun changeEditTextColorWithValidateNickname(nickname: String) {
        val isValidLength = nickname.length in 2..12
        val hasRequiredCharacters = Pattern.compile("^[가-힣a-zA-Z]+$").matcher(nickname).find()

        // 길이 검사 결과에 따라 텍스트 색상을 변경합니다.
        binding.nicknameCheck1.setTextColor(if (isValidLength) ContextCompat.getColor(this, R.color.completeColor) else ContextCompat.getColor(this, R.color.invalid))

        // 필요 문자 검사 결과에 따라 텍스트 색상을 변경합니다.
        binding.nicknameCheck2.setTextColor(if (hasRequiredCharacters) ContextCompat.getColor(this, R.color.completeColor) else ContextCompat.getColor(this, R.color.invalid))
    }
    private fun checkAllInputs() {
        isNicknameValid = isValidNickname(binding.nickname.text.toString())

        binding.submitButton.isEnabled = isNicknameValid

        if (binding.submitButton.isEnabled) {
            binding.submitButton.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
        } else {
            binding.submitButton.setBackgroundColor(ContextCompat.getColor(this, R.color.invalid))
        }
    }
    private fun signUpComplete(nickname: String) {
        Log.d("SignUp", "Starting sign up process with nickname: $nickname")
        val token = getAccessToken()

        if (token != null) {
            val updateRequest = MemberUpdateRequest(
                nickname = nickname,
                signUpStep = "COMPLETE",
                role = "USER"
            )

            val call = RetrofitClient.memberAPIService.updateMember("Bearer $token", updateRequest)
            Log.d("SignUp", "API call created, executing...")

            call.enqueue(object : Callback<MemberResponse> {
                override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                    if (response.isSuccessful) {
                        Log.d("SignUp", "API response successful")
                        response.body()?.let {
                            val intent = Intent(this@NicknameSignUpActivity, DialogJoinFinalBinding::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Log.e("SignUpError", "Error: ${response.code()} - ${response.message()}")
                        handleSignUpError(response)
                    }
                }

                override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                    Toast.makeText(this@NicknameSignUpActivity, "Sign up failed: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("SignUpError", "onFailure: ${t.message}", t)
                }
            })
        } else {
            Toast.makeText(this, "No access token found. Please log in again.", Toast.LENGTH_SHORT).show()
            Log.e("SignUpError", "No access token found.")
        }
    }


    private fun getAccessToken(): String? {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("ACCESS_TOKEN", null)
    }


    private fun handleSignUpError(response: Response<*>) {
        val errorBody = response.errorBody()?.string()
        if (!errorBody.isNullOrEmpty()) {
            try {
                val errorJson = JSONObject(errorBody)
                val errorMessage = errorJson.getString("message")  // Error message를 가져옴

                binding.nicknameErr.text = errorMessage  // Error message를 사용자에게 표시
                binding.nicknameErr.visibility = View.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(this, "회원가입에 실패했습니다.: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "서버 오류로 인해 회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

}