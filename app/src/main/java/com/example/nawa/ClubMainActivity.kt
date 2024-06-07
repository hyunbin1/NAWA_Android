package com.example.nawa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.databinding.ActivityClubMainBinding
import com.example.myapplication.databinding.ActivityCompleteDialogBinding
import com.google.android.material.tabs.TabLayoutMediator

class ClubMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClubMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClubMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val toolbar: Toolbar = binding.clubMainToolbar
        setSupportActionBar(toolbar)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.clubJoinBtn.setOnClickListener{
            showCompletionDialog()
        }

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "클럽 소개"
                1 -> "활동정보"
                2 -> "후기"
                else -> null
            }
        }.attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCompletionDialog() {
        val dialogBinding = ActivityCompleteDialogBinding.inflate(LayoutInflater.from(this))

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        dialogBinding.completeBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}