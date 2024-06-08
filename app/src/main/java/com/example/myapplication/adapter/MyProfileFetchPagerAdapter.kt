package com.example.myapplication.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.activity.FetchIntroduce

class MyProfileFetchPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FetchBasicInfoFragment()
            1 -> FetchIntroduce()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
