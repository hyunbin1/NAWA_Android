package com.example.myapplication.activity.signUp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.data.DTO.Request.InitSignUpRequest
import com.example.myapplication.data.DTO.Response.LoginResponse
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityJoinFirstBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.text.Editable
import android.text.TextWatcher
import com.example.myapplication.R
import org.json.JSONObject
import java.util.regex.Pattern

class InitSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinFirstBinding

    private var isEmailValid = false
    private var isPasswordValid = false
    private var isPasswordMatching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // 각 editText에 포커스가 없어질 때마다 입력값을 검사
        binding.email.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                checkEmail()
                checkAllInputs()
            }
        }

        binding.password.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePassword(binding.password.text.toString())
                checkAllInputs()
            }
        }

        binding.passwordAgain.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                isPasswordMatching(binding.password.text.toString(), binding.passwordAgain.text.toString())
                changeTextIsPasswordMatching(binding.password.text.toString(), binding.passwordAgain.text.toString())
                checkAllInputs()
            }
        }
        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEmail()
                checkAllInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                changeEditTextColorWithValidatePassword(binding.password.text.toString())
                checkAllInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.passwordAgain.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isPasswordMatching(binding.password.text.toString(), binding.passwordAgain.text.toString())
                changeTextIsPasswordMatching(binding.password.text.toString(), binding.passwordAgain.text.toString())
                checkAllInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        binding.nextButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val passwordAgain = binding.passwordAgain.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && passwordAgain.isNotEmpty()) { // 모든 텍스트에 입력 완료
                if (isValidEmail(email) && isValidPassword(password) && isPasswordMatching(password, passwordAgain)) {
                    postSignUp(email, password)
                } else {
                    when {
                        !isValidEmail(email) -> {
                            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                        }
                        !isValidPassword(password) -> {
                            Toast.makeText(this, "비밀번호는 8~16까지 입니다, 영문, 숫자, 특수문자는(!@#\$%^&*()_+-=)이 각 1개씩 필수입니다.", Toast.LENGTH_SHORT).show()
                        }
                        !isPasswordMatching(password, passwordAgain) -> {
                            Toast.makeText(this, "비밀번호가 일치하지 않습니다. ", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "빠짐없이 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAllInputs() {
        isEmailValid = isValidEmail(binding.email.text.toString())
        isPasswordValid = isValidPassword(binding.password.text.toString())
        isPasswordMatching = isPasswordMatching(binding.password.text.toString(), binding.passwordAgain.text.toString())

        binding.nextButton.isEnabled = isEmailValid && isPasswordValid && isPasswordMatching
        if (binding.nextButton.isEnabled) {
            binding.nextButton.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
        } else {
            binding.nextButton.setBackgroundColor(ContextCompat.getColor(this, R.color.invalid))
        }
    }

    private fun checkEmail() {
        val email = binding.email.text.toString().trim()
        if (email.isEmpty()) {
            binding.emailError.text = "이메일을 입력해주세요"
            binding.emailError.visibility = View.VISIBLE
        } else if (!isValidEmail(email)) {
            binding.emailError.text = "유효하지 않은 이메일입니다"
            binding.emailError.visibility = View.VISIBLE
        } else {
            binding.emailError.visibility = View.GONE
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String) {
        val isValidLength = password.length in 8..16
        val hasRequiredCharacters = Pattern.compile("^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+=-]).*$").matcher(password).find()

        binding.passwordError.text = when {
            !isValidLength -> "비밀번호는 8자 이상 16자 이하이어야 합니다."
            !hasRequiredCharacters -> "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
            else -> ""
        }
        binding.passwordError.visibility = if (binding.passwordError.text.isNotEmpty()) View.VISIBLE else View.GONE
    }
    private fun changeEditTextColorWithValidatePassword(password: String) {
        val isValidLength = password.length in 8..16
        val hasRequiredCharacters = Pattern.compile("^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+=-]).*$").matcher(password).find()

        // 길이 검사 결과에 따라 텍스트 색상을 변경합니다.
        binding.passwordLengthCheck.setTextColor(if (isValidLength) ContextCompat.getColor(this, R.color.completeColor) else ContextCompat.getColor(this, R.color.invalid))

        // 필요 문자 검사 결과에 따라 텍스트 색상을 변경합니다.
        binding.passwordCharacterCheck.setTextColor(if (hasRequiredCharacters) ContextCompat.getColor(this, R.color.completeColor) else ContextCompat.getColor(this, R.color.invalid))
        binding.passwordCheck3.setTextColor(if (hasRequiredCharacters) ContextCompat.getColor(this, R.color.completeColor) else ContextCompat.getColor(this, R.color.invalid))
    }


    private fun isValidPassword(password: String): Boolean {
        val isValidLength = password.length in 8..16
        val hasRequiredCharacters = Pattern.compile("^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+=-]).*$").matcher(password).find()
        return isValidLength && hasRequiredCharacters
    }

    private fun isPasswordMatching(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
    private fun changeTextIsPasswordMatching(password: String, confirmPassword: String): Boolean {
        val isMatching = password == confirmPassword

        binding.passwordAgainErr.text = when {
            !isMatching -> "비밀번호가 일치하지 않습니다."
            else -> "비밀번호가 일치합니다."
        }

        binding.passwordAgainErr.setTextColor(if (isMatching) ContextCompat.getColor(this, R.color.completeColor) else ContextCompat.getColor(this, R.color.warn))
        binding.passwordAgainErr.visibility = if (binding.passwordError.text.isNotEmpty()) View.VISIBLE else View.GONE
        return isMatching
    }



    private fun postSignUp(emailId: String, password: String) {
        val initSignUpRequest = InitSignUpRequest(emailId, password, false)
        val call = RetrofitClient.memberAPIService.initSignUp(initSignUpRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        saveAccessToken(it.accessToken)
                        val intent = Intent(this@InitSignUpActivity, NicknameSignUpActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    handleSignUpError(response)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@InitSignUpActivity, "Sign up failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleSignUpError(response: Response<*>) {
        val errorBody = response.errorBody()?.string()
        if (!errorBody.isNullOrEmpty()) {
            try {
                val errorJson = JSONObject(errorBody)
                val errorMessage = errorJson.getString("message")  // Error message를 가져옴

                binding.emailError.text = errorMessage  // Error message를 사용자에게 표시
                binding.emailError.visibility = View.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(this, "Sign up failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Sign up failed: Unknown error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAccessToken(token: String) {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("ACCESS_TOKEN", token)
        editor.apply()
    }
}
