package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.data.database.Club
import com.example.myapplication.databinding.ListMypageclubBinding

class MyPageClubAdapter : RecyclerView.Adapter<MyPageClubAdapter.ClubViewHolder>() {

    private var clubs: List<Club> = listOf()

    fun setClubs(clubs: List<Club>) {
        this.clubs = clubs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val binding = ListMypageclubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        holder.bind(clubs[position])
    }

    override fun getItemCount(): Int = clubs.size

    inner class ClubViewHolder(private val binding: ListMypageclubBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(club: Club) {
            binding.clubname.text = club.clubName
            Glide.with(binding.clubImage.context)
                .load(club.clubLogo)
                .into(binding.clubImage)
        }
    }
}
