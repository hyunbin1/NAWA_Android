package com.example.myapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.activity.ClubMainActivity
import com.example.myapplication.data.DTO.Request.ClubBannerDTO
import com.example.myapplication.databinding.ItemClubBinding

class ClubBannerAdapter : RecyclerView.Adapter<ClubBannerAdapter.ClubViewHolder>() {

    private var clubs: MutableList<ClubBannerDTO> = mutableListOf()

    fun setClubs(clubs: List<ClubBannerDTO>) {
        this.clubs = clubs.toMutableList()
        notifyDataSetChanged()
    }

    fun addClubs(clubs: List<ClubBannerDTO>) {
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
        fun bind(club: ClubBannerDTO) {
            binding.clubName.text = club.clubName
            Glide.with(binding.root.context)
                .load(club.clubLogo)
                .circleCrop() // 이미지 모양 원형
                .placeholder(R.drawable.ic_launcher_background) // 기본 이미지 설정
                .error(R.drawable.ic_launcher_background) // 오류 발생 시 기본 이미지 설정
                .into(binding.clubLogo)

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ClubMainActivity::class.java)
                intent.putExtra("CLUB_UUID", club.clubUUID)
                context.startActivity(intent)
            }
        }
    }
}
