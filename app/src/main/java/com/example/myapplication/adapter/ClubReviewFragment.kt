package com.example.myapplication.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.data.database.Club
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ClubReviewBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClubReviewFragment : Fragment() {
    private var _binding: ClubReviewBinding? = null
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
        _binding = ClubReviewBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchClubReview(clubUUID)
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
    private fun fetchClubReview(clubUUID: String) {
        val call = RetrofitClient.apiService.getClubDetail(clubUUID)
        call.enqueue(object : Callback<Club> {
            override fun onResponse(call: Call<Club>, response: Response<Club>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        displayClubReview(it)
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

    private fun displayClubReview(club: Club) {
    }
}