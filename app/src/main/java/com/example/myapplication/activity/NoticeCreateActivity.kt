package com.example.myapplication.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.data.database.Notification
import com.example.myapplication.data.database.enum.NotificationCategory
import com.example.myapplication.data.helper.NoticeDbHelper
import com.example.myapplication.databinding.ActivityCreateNoticeBinding
import java.util.*

class NoticeCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNoticeBinding
    private lateinit var dbHelper: NoticeDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("NoticeCreateActivity", "onCreate called")
        binding = ActivityCreateNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = NoticeDbHelper(this)

        val createButton: Button = binding.createClubButton
        val titleEditText: EditText = binding.clubNameEditText
        val contentEditText: EditText = binding.clubDescriptionEditText
        val categoryRadioGroup: RadioGroup = binding.categoryRadioGroup

        createButton.setOnClickListener {
            Log.d("NoticeCreateActivity", "Create button clicked")
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            val category = when (categoryRadioGroup.checkedRadioButtonId) {
                R.id.category1 -> NotificationCategory.EVENT
                R.id.category2 -> NotificationCategory.ALERT
                else -> NotificationCategory.GENERAL
            }
            val currentTime = System.currentTimeMillis().toString()

            val noticeUUID = UUID.randomUUID().toString()

            try {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put(NoticeDbHelper.COLUMN_NOTICE_UUID, noticeUUID)
                    put(NoticeDbHelper.COLUMN_CATEGORY, category.name) // Save the enum name as a string
                    put(NoticeDbHelper.COLUMN_TITLE, title)
                    put(NoticeDbHelper.COLUMN_CONTENT, content)
                    put(NoticeDbHelper.COLUMN_PINNED, 0)
                    put(NoticeDbHelper.COLUMN_PINNED_AT, "")
                    put(NoticeDbHelper.COLUMN_VIEW_COUNT, 0)
                    put(NoticeDbHelper.COLUMN_CREATE_AT, currentTime)
                    put(NoticeDbHelper.COLUMN_UPDATED_AT, "")
                }

                val newRowId = db.insert(NoticeDbHelper.TABLE_NAME, null, values)
                Log.d("NoticeCreateActivity", "newRowId: $newRowId") // newRowId 로그 추가

                if (newRowId != -1L) {
                    // 저장 성공
                    Log.d("NoticeCreateActivity", "저장된 공지사항 id: $newRowId")
                    val intent = Intent(this@NoticeCreateActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("NoticeCreateActivity", "id값 오류")
                }
            } catch (e: Exception) {
                Log.e("NoticeCreateActivity", "데이터 삽입 실패", e)
            }
        }
    }
}
