package com.example.myapplication.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.database.Notification
import com.example.myapplication.data.helper.NoticeDbHelper
import com.example.myapplication.databinding.ListNoticeBinding
import com.example.myapplication.adapter.NoticeAdapter
import com.example.myapplication.data.database.enum.NotificationCategory

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
        val dbHelper = NoticeDbHelper(this)
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            NoticeDbHelper.TABLE_NAME,
            null, // 모든 열을 선택
            null, // 조건 없음
            null, // 조건 값 없음
            null, // 그룹핑하지 않음
            null, // 그룹핑 조건 값 없음
            "${NoticeDbHelper.COLUMN_CREATE_AT} DESC" // 최신 순으로 정렬
        )

        val notices = mutableListOf<Notification>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_ID))
                val noticeUUID = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_NOTICE_UUID))
                val category = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_CATEGORY))
                val title = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_TITLE))
                val content = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_CONTENT))
                val pinned = getInt(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_PINNED)) > 0
                val pinnedAt = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_PINNED_AT))
                val viewCount = getInt(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_VIEW_COUNT))
                val createAt = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_CREATE_AT))
                val updatedAt = getString(getColumnIndexOrThrow(NoticeDbHelper.COLUMN_UPDATED_AT))

                val notice = Notification(
                    id,
                    noticeUUID,
                    NotificationCategory.valueOf(category),
                    title,
                    content,
                    pinned,
                    pinnedAt,
                    viewCount,
                    createAt,
                    updatedAt
                )
                notices.add(notice)
            }
        }
        cursor.close()

        noticeAdapter.setNotices(notices)
    }
}
