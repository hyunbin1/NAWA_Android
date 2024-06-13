package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.activity.MyProfileActivity
import com.example.myapplication.data.DTO.Request.MemberUpdateRequest
import com.example.myapplication.data.DTO.Response.MemberResponse
import com.example.myapplication.data.model.Member
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ProfileFetchIntroduceBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FetchIntroduceFragment : Fragment() {

    private var _binding: ProfileFetchIntroduceBinding? = null
    private val binding get() = _binding!!
    private var currentIntroduce: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileFetchIntroduceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fetchButton2.setOnClickListener {
            Log.d("FetchIntroduceFragment", "버튼 클릭")
            updateMemberIntro()
        }

        fetchMemberIntro()
    }

    private fun fetchMemberIntro() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        accessToken?.let { token ->
            val call = RetrofitClient.apiService.getMemberInfo("Bearer $token")
            call.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        response.body()?.let { member ->
                            currentIntroduce = member.introduce

                            binding.introduce.setText(member.introduce)
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

    private fun updateMemberIntro() {
        val introduce = if (binding.introduce.text.isNullOrEmpty()) currentIntroduce else binding.introduce.text.toString()

        val sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (accessToken != null) {
            val request = MemberUpdateRequest(
                introduce = introduce,
            )
            val call = RetrofitClient.memberAPIService.updateMember("Bearer $accessToken", request)

            call.enqueue(object : Callback<MemberResponse> {
                override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Log.d("FetchIntroduceFragment", "Update successful: $it")
                            Toast.makeText(requireContext(), "정보가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show()
                            navigateToMyProfile()
                        }
                    } else {
                        Log.e("FetchIntroduceFragment", "Update failed: ${response.code()} - ${response.message()}")
                        Log.e("FetchIntroduceFragment", "Response body: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "20자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                    Log.e("FetchIntroduceFragment", "Update failed with error: ${t.message}")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
