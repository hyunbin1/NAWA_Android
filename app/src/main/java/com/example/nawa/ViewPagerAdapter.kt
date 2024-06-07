package com.example.nawa

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ClubIntroFragment()
            1 -> ClubInfoFragment()
            2 -> ClubReviewFragment()
            else -> ClubInfoFragment()
        }
    }
}
