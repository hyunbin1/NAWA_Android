package com.example.myapplication.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.data.DTO.Request.MemberUpdateRequest
import com.example.myapplication.data.DTO.Request.ProfileUpdateRequest
import com.example.myapplication.data.DTO.Response.MemberResponse
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ProfileFetchBasicinfoBinding
import retrofit2.Call
import retrofit2.Response

class FetchBasicInfoFragment : Fragment() {

    private var _binding: ProfileFetchBasicinfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileFetchBasicinfoBinding.inflate(inflater, container, false)
        return binding.root
    }

//    private fun updateMemberInfo() {
//        val age = binding.age.text.toString().toInt()
//        val mbti = binding.mbti.text.toString()
//        val hobby = binding.hobby.text.toString()
//        val job = binding.job.text.toString()
//
//        val updateRequest = MemberUpdateRequest(
//
//            age = age,
//            mbti = mbti,
//            hobby = hobby,
//            job = job
//        )
//
//        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
//        val token = sharedPreferences.getString("ACCESS_TOKEN", null)
//
//        if (token != null) {
//            val call = RetrofitClient.memberAPIService.updateMember("Bearer $token", updateRequest)
//
//            call.enqueue(object : retrofit2.Callback<MemberResponse> {
//                override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
//                    if (response.isSuccessful) {
//                        val updatedMember = response.body()
//                        updatedMember?.let {
//                            Toast.makeText(requireContext(), "프로필이 업데이트 되었습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        Log.e("FetchBasicInfoFragment", "프로필 업데이트 실패: ${response.message()}")
//                        Toast.makeText(requireContext(), "프로필 업데이트 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
//                    Log.e("FetchBasicInfoFragment", "프로필 업데이트 중 오류 발생: ${t.message}")
//                    Toast.makeText(requireContext(), "프로필 업데이트 중 오류 발생: ${t.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
