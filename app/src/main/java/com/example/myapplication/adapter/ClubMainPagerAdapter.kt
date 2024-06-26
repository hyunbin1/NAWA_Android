package com.example.myapplication.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.adapter.ClubDetailFragment

class ClubMainPagerAdapter(fragmentActivity: FragmentActivity, private val clubUUID: String, private val isSqlite: Boolean) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 1
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ClubDetailFragment.newInstance(clubUUID, isSqlite)
            else -> ClubDetailFragment.newInstance(clubUUID, isSqlite)
        }
    }
}
