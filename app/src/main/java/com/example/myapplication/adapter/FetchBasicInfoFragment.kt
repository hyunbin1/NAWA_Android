package com.example.myapplication.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.myapplication.R

import com.example.myapplication.databinding.ProfileFetchBasicinfoBinding

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기 상태 설정
        binding.maleButton.setBackgroundResource(R.drawable.button_sex_select)
        binding.femaleButton.setBackgroundResource(R.drawable.button_sex)

        // 버튼 클릭 리스너 설정
        binding.maleButton.setOnClickListener {
            selectGenderButton(binding.maleButton, binding.femaleButton)
        }

        binding.femaleButton.setOnClickListener {
            selectGenderButton(binding.femaleButton, binding.maleButton)
        }
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
