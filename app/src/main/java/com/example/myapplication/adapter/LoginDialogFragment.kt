package com.example.myapplication.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.myapplication.activity.LoginActivity
import com.example.myapplication.databinding.ActivityLoginDialogBinding

class LoginDialogFragment : DialogFragment() {

    private lateinit var binding: ActivityLoginDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityLoginDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        /** goToJoin 버튼 클릭 시 activity_login 페이지로 이동.
         * 액티비티는 LoginActivity 로 넘김 */
        binding.goToJoin.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            dismiss()
        }
    }
}
