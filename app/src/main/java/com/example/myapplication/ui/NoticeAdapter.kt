package com.example.myapplication.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.model.Notice
import com.example.myapplication.databinding.ItemNoticeBinding

class NoticeAdapter : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    private var notices: List<Notice> = listOf()

    fun setNotices(notices: List<Notice>) {
        this.notices = notices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(notices[position])
    }

    override fun getItemCount(): Int = notices.size

    inner class NoticeViewHolder(private val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: Notice) {
            binding.title.text = if (notice.title.length > 20) notice.title.substring(0, 20) + "..." else notice.title
            binding.createDate.text = notice.createAt.substring(0, 10) // Assuming the date format is "yyyy-MM-dd"
            binding.viewCount.text = "조회수: ${notice.viewCount}"

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, NoticeDetailActivity::class.java)
                intent.putExtra("NOTICE_ID", notice.id)
                context.startActivity(intent)
            }
        }
    }
}
