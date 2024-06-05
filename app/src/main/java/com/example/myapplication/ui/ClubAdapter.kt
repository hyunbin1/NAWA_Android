package com.example.myapplication.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.model.Club
import com.example.myapplication.databinding.ItemClubBinding

class ClubAdapter : RecyclerView.Adapter<ClubAdapter.ClubViewHolder>() {

    private var clubs: MutableList<Club> = mutableListOf()

    fun setClubs(clubs: List<Club>) {
        this.clubs = clubs.toMutableList()
        notifyDataSetChanged()
    }

    fun addClubs(clubs: List<Club>) {
        val currentList = this.clubs.toMutableList()
        currentList.addAll(clubs)
        this.clubs = currentList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val binding = ItemClubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        holder.bind(clubs[position])
    }

    override fun getItemCount(): Int {
        return clubs.size
    }

    class ClubViewHolder(private val binding: ItemClubBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(club: Club) {
            binding.clubName.text = club.clubName
            // Glide를 사용하여 clubLogo 이미지 로드
            Glide.with(binding.root.context)
                .load(club.clubLogo)
                .placeholder(R.drawable.ic_launcher_background) // 기본 이미지 설정
                .error(R.drawable.ic_launcher_background) // 오류 발생 시 기본 이미지 설정
                .into(binding.clubLogo)


            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ClubDetailActivity::class.java)
                intent.putExtra("CLUB_UUID", club.clubUUID)
                context.startActivity(intent)
            }
        }
    }
}
