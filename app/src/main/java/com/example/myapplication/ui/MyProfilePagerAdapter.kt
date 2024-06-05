package com.example.myapplication.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyProfilePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2 // 기본 정보와 소개 탭 2개
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileBasicInfoFragment()
            1 -> ProfileIntroduceFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
