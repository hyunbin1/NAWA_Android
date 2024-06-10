package com.example.myapplication.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.data.database.Club
import com.example.myapplication.data.helper.ClubDbHelper
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ClubIntroBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClubDetailFragment : Fragment() {

    private var _binding: ClubIntroBinding? = null
    private val binding get() = _binding!!
    private lateinit var clubUUID: String
    private var isSqlite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            clubUUID = it.getString("CLUB_UUID") ?: ""
            isSqlite = it.getBoolean("IS_SQLITE", false)
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
        fetchClubDetail(clubUUID, isSqlite)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(clubUUID: String, isSqlite: Boolean) =
            ClubDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("CLUB_UUID", clubUUID)
                    putBoolean("IS_SQLITE", isSqlite)
                }
            }
    }

    private fun fetchClubDetail(clubUUID: String, isSqlite: Boolean) {
        if (isSqlite) {
            fetchClubDetailFromDb(clubUUID)
        } else {
            fetchClubDetailFromApi(clubUUID)
        }
    }

    private fun fetchClubDetailFromDb(clubUUID: String) {
        val dbHelper = ClubDbHelper(requireContext())
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            ClubDbHelper.TABLE_NAME,
            arrayOf(
                ClubDbHelper.COLUMN_CLUB_UUID,
                ClubDbHelper.COLUMN_CLUB_NAME,
                ClubDbHelper.COLUMN_CLUB_INTRO,
                ClubDbHelper.COLUMN_CLUB_LOGO,
                ClubDbHelper.COLUMN_CLUB_INTRODUCTION,
                ClubDbHelper.COLUMN_CLUB_QUALIFICATION,
                ClubDbHelper.COLUMN_CLUB_REGIS_PROCESS,
                ClubDbHelper.COLUMN_CLUB_NOTICE,
                ClubDbHelper.COLUMN_CLUB_CANCEL_INTRODUCTION,
                ClubDbHelper.COLUMN_CLUB_PRICE,
                ClubDbHelper.COLUMN_MEMBER_COUNT,
                ClubDbHelper.COLUMN_CREATE_AT,
                ClubDbHelper.COLUMN_UPDATED_AT
            ),
            "${ClubDbHelper.COLUMN_CLUB_UUID} = ?",
            arrayOf(clubUUID),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val clubUUID = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_UUID))
            val clubName = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_NAME))
            val clubIntro = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_INTRO))
            val clubLogo = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_LOGO))
            val clubIntroduction = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_INTRODUCTION))
            val clubQualification = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_QUALIFICATION))?.split(",")
            val clubRegisProcess = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_REGIS_PROCESS))
            val clubNotice = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_NOTICE))
            val clubCancelIntroduction = cursor.getString(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_CANCEL_INTRODUCTION))?.split(",")
            val clubPrice = cursor.getInt(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_CLUB_PRICE))
            val memberCount = cursor.getInt(cursor.getColumnIndexOrThrow(ClubDbHelper.COLUMN_MEMBER_COUNT))

            val club = Club(
                id = 0,
                clubUUID = clubUUID,
                clubName = clubName,
                clubIntro = clubIntro,
                clubLogo = clubLogo,
                clubIntroduction = clubIntroduction,
                clubQualification = clubQualification,
                clubRegisProcess = clubRegisProcess,
                clubNotice = clubNotice,
                clubCancelIntroduction = clubCancelIntroduction,
                clubPrice = clubPrice,
                memberCount = memberCount
            )
            displayClubDetail(club)
        }
        cursor.close()
    }

    private fun fetchClubDetailFromApi(clubUUID: String) {
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

    fun displayClubDetail(club: Club) {
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
            Log.d("ClubDetailFragment", "클럽 소개 바인딩 성공")
        } else {
            binding.clubCancelIntroduction.text = "" // 기본값 설정
        }
    }
}
