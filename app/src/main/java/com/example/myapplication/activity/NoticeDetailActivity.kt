package com.example.myapplication.activity

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.data.database.Notification
import com.example.myapplication.data.database.enum.NotificationCategory
import com.example.myapplication.data.helper.NoticeDbHelper
import com.example.myapplication.data.model.Member
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.databinding.ActivityNoticeDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeDetailBinding
    private lateinit var dbHelper: NoticeDbHelper
    private var noticeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noticeId = intent.getIntExtra("NOTICE_ID", -1)

        dbHelper = NoticeDbHelper(this)

        if (noticeId != -1) {
            fetchNoticeDetails(noticeId)
        } else {
            // 오류 처리
            Toast.makeText(this, "공지사항을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        fetchMemberInfo()
    }

    private fun fetchNoticeDetails(noticeId: Int) {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            NoticeDbHelper.TABLE_NAME,
            null, // 모든 열을 선택
            "${NoticeDbHelper.COLUMN_ID} = ?", // 조건
            arrayOf(noticeId.toString()), // 조건 값
            null, // 그룹핑하지 않음
            null, // 그룹핑 조건 값 없음
            null // 정렬 조건 없음
        )

        if (cursor.moveToFirst()) {
            val noticeUUID = cursor.getString(cursor.getColumnIndexOrThrow(NoticeDbHelper.COLUMN_NOTICE_UUID))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(NoticeDbHelper.COLUMN_CATEGORY))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(NoticeDbHelper.COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(NoticeDbHelper.COLUMN_CONTENT))
            val pinned = cursor.getInt(cursor.getColumnIndexOrThrow(NoticeDbHelper.COLUMN_PINNED)) > 0
            val pinnedAt = cursor.getString(cursor.getColumnIndexOrThrow(NoticeDbHelper.COLUMN_PINNED_AT))
            val viewCount = cursor.getInt(cursor.getColumnIndexOrThrow(NoticeDbHelper.COLUMN_VIEW_COUNT))
            val createAt = cursor.getString(cursor.getColumnIndexOrThrow(NoticeDbHelper.COLUMN_CREATE_AT))
            val updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(NoticeDbHelper.COLUMN_UPDATED_AT))

            val notice = Notification(
                noticeId,
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

            bindNoticeDetails(notice)
        }
        cursor.close()
    }

    private fun bindNoticeDetails(notice: Notification) {
        binding.noticeCategory.text = notice.category.name
        binding.noticeTitle.text = notice.title
        binding.noticeContent.text = notice.content
    }

    private fun deleteNotice(noticeId: Int) {
        val db = dbHelper.writableDatabase
        val deletedRows = db.delete(
            NoticeDbHelper.TABLE_NAME,
            "${NoticeDbHelper.COLUMN_ID} = ?",
            arrayOf(noticeId.toString())
        )

        if (deletedRows > 0) {
            Toast.makeText(this, "공지사항이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "공지사항 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchMemberInfo() {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        accessToken?.let { token ->
            val call = RetrofitClient.apiService.getMemberInfo("Bearer $token")
            call.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        response.body()?.let { member ->
                            Log.d("MyProfileActivity", member.role)
                            if (member.role == "admin") {
                                binding.deleteButton.visibility = View.VISIBLE
                                binding.deleteButton.setOnClickListener {
                                    deleteNotice(noticeId)
                                }
                            }
                        } ?: run {
                            Log.e("MyProfileActivity", "회원 정보 응답이 없습니다.")
                        }
                    } else {
                        Log.e("MyProfileActivity", "회원 정보를 가져오지 못했습니다: ${response.code()} - ${response.message()}")
                        Toast.makeText(this@NoticeDetailActivity, "회원 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    Log.e("MyProfileActivity", "회원 정보를 가져오는 중 오류가 발생했습니다: ${t.message}")
                    Toast.makeText(this@NoticeDetailActivity, "회원 정보를 가져오는 중 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
