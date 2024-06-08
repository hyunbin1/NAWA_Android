package com.example.myapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.activity.NoticeDetailActivity
import com.example.myapplication.data.database.Notification
import com.example.myapplication.databinding.ItemNoticeBinding

class NoticeAdapter : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    private var notices: List<Notification> = listOf()

    fun setNotices(notices: List<Notification>) {
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
        fun bind(notice: Notification) {
            binding.noticeCategory.text = notice.category.name
            binding.title.text = if (notice.title.length > 20) notice.title.substring(0, 20) + "..." else notice.title
            binding.createDate.text = notice.createAt.substring(0, 10)
            binding.viewCount.text = "${notice.viewCount}"

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, NoticeDetailActivity::class.java)
                intent.putExtra("NOTICE_ID", notice.id)
                context.startActivity(intent)
            }
        }
    }
}
