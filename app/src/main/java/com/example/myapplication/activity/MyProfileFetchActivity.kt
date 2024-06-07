package com.example.myapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMyprofileFetchBinding
import com.google.android.material.tabs.TabLayoutMediator

class MyProfileFetchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyprofileFetchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyprofileFetchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupTabLayout()
    }

    private fun setupViewPager() {
        val adapter = MyProfileFetchPagerAdapter(this)
        binding.viewpager.adapter = adapter
    }

    private fun setupTabLayout() {
        val tabTitles = arrayOf(getString(R.string.basicinfo), getString(R.string.introduce))
        TabLayoutMediator(binding.tab, binding.viewpager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}
