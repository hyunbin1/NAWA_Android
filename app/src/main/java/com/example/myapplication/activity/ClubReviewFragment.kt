package com.example.myapplication.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ClubReviewBinding
import com.example.myapplication.fragment.ClubDetailFragment

class ClubReviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ClubReviewBinding.inflate(inflater, container, false).root
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
}