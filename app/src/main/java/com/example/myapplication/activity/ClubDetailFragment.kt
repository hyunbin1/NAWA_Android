package com.example.myapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.data.database.Club
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ClubIntroBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClubDetailFragment : Fragment() {

    private var _binding: ClubIntroBinding? = null
    private val binding get() = _binding!!
    private lateinit var clubUUID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            clubUUID = it.getString("CLUB_UUID") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClubIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchClubDetail(clubUUID)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(clubUUID: String) =
            ClubDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("CLUB_UUID", clubUUID)
                }
            }
    }

    private fun fetchClubDetail(clubUUID: String) {
        val call = RetrofitClient.apiService.getClubDetail(clubUUID)
        call.enqueue(object : Callback<Club> {
            override fun onResponse(call: Call<Club>, response: Response<Club>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        displayClubDetail(it)
                    }
                } else {
                    // Log error
                }
            }

            override fun onFailure(call: Call<Club>, t: Throwable) {
                // Log error
            }
        })
    }

    private fun displayClubDetail(club: Club) {
        binding.introduceClub.text = club.clubIntroduction // 클럽 소개
        val clubQualificationList = club.clubQualification // 가입 조건
        if (clubQualificationList != null) {
            val clubQualificationText = clubQualificationList.joinToString(separator = "\n")
            binding.joinCondition.text = clubQualificationText
        } else {
            binding.joinCondition.text = "" // 기본값 설정
        }
        binding.clubRegisProcess.text = club.clubRegisProcess // 등록 절차
        binding.clubNotice.text = club.clubNotice // 유의사항
        val clubCancelIntroductionList = club.clubCancelIntroduction
        if (clubCancelIntroductionList != null) {
            val clubCancelIntroductionText = clubCancelIntroductionList.joinToString(separator = "\n")
            binding.clubCancelIntroduction.text = clubCancelIntroductionText
        } else {
            binding.clubCancelIntroduction.text = "" // 기본값 설정
        }
    }
}
