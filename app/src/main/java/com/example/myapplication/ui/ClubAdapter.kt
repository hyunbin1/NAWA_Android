
package com.example.myapplication.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.data.model.Club
import com.example.myapplication.databinding.ItemClubBinding

class ClubAdapter : RecyclerView.Adapter<ClubAdapter.ClubViewHolder>() {

    private var clubs: List<Club> = listOf()

    fun setClubs(clubs: List<Club>) {
        this.clubs = clubs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val binding = ItemClubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        holder.bind(clubs[position])
    }

    override fun getItemCount(): Int = clubs.size

    inner class ClubViewHolder(private val binding: ItemClubBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(club: Club) {
            binding.clubName.text = club.clubName
            Glide.with(binding.clubLogo.context)
                .load(club.clubLogo)
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
