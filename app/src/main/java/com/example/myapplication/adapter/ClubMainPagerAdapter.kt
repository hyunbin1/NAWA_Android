package com.example.myapplication.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ClubMainPagerAdapter(fragmentActivity: FragmentActivity, private val clubUUID: String) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ClubDetailFragment.newInstance(clubUUID)
            1 -> ClubReviewFragment.newInstance(clubUUID)
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
