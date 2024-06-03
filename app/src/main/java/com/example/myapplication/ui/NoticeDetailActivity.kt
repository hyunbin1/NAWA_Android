package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.data.model.Notice
import com.example.myapplication.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeDetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_detail)

        titleTextView = findViewById(R.id.noticeTitle)
        contentTextView = findViewById(R.id.noticeContent)

        val noticeId = intent.getIntExtra("NOTICE_ID", -1)
        if (noticeId != -1) {
            fetchNoticeDetail(noticeId)
        } else {
            Toast.makeText(this, "유효하지 않은 공지사항 ID입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchNoticeDetail(noticeId: Int) {
        val call = RetrofitClient.apiService.getNoticeDetail(noticeId)
        call.enqueue(object : Callback<Notice> {
            override fun onResponse(call: Call<Notice>, response: Response<Notice>) {
                if (response.isSuccessful) {
                    response.body()?.let { notice ->
                        titleTextView.text = notice.title
                        contentTextView.text = notice.content
                        Log.d("NoticeDetailActivity", "공지사항을 가져왔습니다: ID = ${notice.id}")
                    } ?: run {
                        Log.e("NoticeDetailActivity", "공지사항 응답이 없습니다.")
                        Toast.makeText(this@NoticeDetailActivity, "공지사항 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("NoticeDetailActivity", "공지사항을 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@NoticeDetailActivity, "공지사항을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Notice>, t: Throwable) {
                Log.e("NoticeDetailActivity", "공지사항을 가져오는 중 오류가 발생했습니다: ${t.message}")
                Toast.makeText(this@NoticeDetailActivity, "공지사항을 가져오는 중 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
