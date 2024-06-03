package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.model.Notice
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ListNoticeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeListActivity : AppCompatActivity() {

    private lateinit var binding: ListNoticeBinding
    private lateinit var noticeAdapter: NoticeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchAllNotices()
    }

    private fun setupRecyclerView() {
        noticeAdapter = NoticeAdapter()
        binding.noticeRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@NoticeListActivity)
            adapter = noticeAdapter
        }
    }

    private fun fetchAllNotices() {
        val call = RetrofitClient.apiService.getNotices()
        call.enqueue(object : Callback<List<Notice>> {
            override fun onResponse(call: Call<List<Notice>>, response: Response<List<Notice>>) {
                if (response.isSuccessful) {
                    response.body()?.let { notices ->
                        noticeAdapter.setNotices(notices)
                    } ?: run {
                        Log.e("NoticeListActivity", "공지사항 응답이 없습니다.")
                    }
                } else {
                    Log.e("NoticeListActivity", "공지사항을 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@NoticeListActivity, "공지사항을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Notice>>, t: Throwable) {
                Log.e("NoticeListActivity", "공지사항을 가져오는 중 오류가 발생했습니다: ${t.message}")
                Toast.makeText(this@NoticeListActivity, "공지사항을 가져오는 중 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
