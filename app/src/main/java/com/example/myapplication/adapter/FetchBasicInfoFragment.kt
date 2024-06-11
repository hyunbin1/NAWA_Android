package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.activity.MyProfileActivity
import com.example.myapplication.data.DTO.Request.MemberUpdateRequest
import com.example.myapplication.data.DTO.Response.MemberResponse
import com.example.myapplication.data.model.Member
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ProfileFetchBasicinfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FetchBasicInfoFragment : Fragment() {

    private var _binding: ProfileFetchBasicinfoBinding? = null
    private val binding get() = _binding!!
    private var selectedGender: String = "male"
    private var currentAge: String? = null
    private var currentMbti: String? = null
    private var currentHobby: String? = null
    private var currentJob: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileFetchBasicinfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기 상태 설정
        binding.maleButton.setBackgroundResource(R.drawable.button_sex_select)
        binding.femaleButton.setBackgroundResource(R.drawable.button_sex)

        // 버튼 클릭 리스너 설정
        binding.maleButton.setOnClickListener {
            selectGenderButton(binding.maleButton, binding.femaleButton)
            selectedGender = "male"
        }

        binding.femaleButton.setOnClickListener {
            selectGenderButton(binding.femaleButton, binding.maleButton)
            selectedGender = "female"
        }

        binding.saveButton.setOnClickListener {
            updateMemberInfo()
        }

        fetchMemberInfo()
    }

    private fun fetchMemberInfo() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        accessToken?.let { token ->
            val call = RetrofitClient.apiService.getMemberInfo("Bearer $token")
            call.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        response.body()?.let { member ->
                            currentAge = member.age.toString()
                            currentMbti = member.mbti
                            currentHobby = member.hobby
                            currentJob = member.job

                            binding.age.setText(member.age.toString())
                            binding.mbti.setText(member.mbti)
                            binding.hobby.setText(member.hobby)
                            binding.job.setText(member.job)

                            if (member.gender == "male") {
                                selectGenderButton(binding.maleButton, binding.femaleButton)
                                selectedGender = "male"
                            } else {
                                selectGenderButton(binding.femaleButton, binding.maleButton)
                                selectedGender = "female"
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "회원 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    Toast.makeText(requireContext(), "회원 정보를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updateMemberInfo() {
        val age = if (binding.age.text.isNullOrEmpty()) currentAge else binding.age.text.toString()
        val mbti = if (binding.mbti.text.isNullOrEmpty()) currentMbti else binding.mbti.text.toString()
        val hobby = if (binding.hobby.text.isNullOrEmpty()) currentHobby else binding.hobby.text.toString()
        val job = if (binding.job.text.isNullOrEmpty()) currentJob else binding.job.text.toString()

        val sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (accessToken != null) {
            val request = MemberUpdateRequest(
                age = age?.toInt(),
                gender = selectedGender,
                mbti = mbti,
                hobby = hobby,
                job = job
            )
            val call = RetrofitClient.memberAPIService.updateMember("Bearer $accessToken", request)

            call.enqueue(object : Callback<MemberResponse> {
                override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Toast.makeText(requireContext(), "정보가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show()
                            navigateToMyProfile()
                        }
                    } else {
                        Toast.makeText(requireContext(), "업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "업데이트 중 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMyProfile() {
        val intent = Intent(requireContext(), MyProfileActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun selectGenderButton(selectedButton: Button, unselectedButton: Button) {
        selectedButton.setBackgroundResource(R.drawable.button_sex_select)
        selectedButton.setTextColor(Color.WHITE)
        unselectedButton.setBackgroundResource(R.drawable.button_sex)
        unselectedButton.setTextColor(Color.GRAY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
