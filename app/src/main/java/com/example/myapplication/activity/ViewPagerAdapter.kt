package com.example.myapplication.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nawa.ClubInfoFragment
import com.example.nawa.ClubIntroFragment
import com.example.nawa.ClubReviewFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ClubIntroFragment()
//            1 -> ClubInfoFragment()
            1 -> ClubReviewFragment()
            else -> ClubInfoFragment()
        }
    }
}
