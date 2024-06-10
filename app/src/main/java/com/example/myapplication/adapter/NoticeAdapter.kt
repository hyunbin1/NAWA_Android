package com.example.myapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.activity.NoticeDetailActivity
import com.example.myapplication.data.database.Notification
import com.example.myapplication.databinding.ItemNoticeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

            // createAt 날짜 포맷팅
            val date = Date(notice.createAt.toLong())
            val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val formattedDate = outputFormat.format(date)
            binding.createDate.text = formattedDate

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
