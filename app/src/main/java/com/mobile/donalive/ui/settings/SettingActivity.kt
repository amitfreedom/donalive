package com.mobile.donalive.ui.settings

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.mobile.donalive.R
import com.mobile.donalive.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private var _binding: ActivitySettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChangePassword.setOnClickListener {
            passwordDialog()
        }
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun passwordDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.change_password_layout)
        val saveBtn = dialog.findViewById(R.id.btn_save) as Button
        val noBtn = dialog.findViewById(R.id.btn_cancel) as Button
        saveBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
        val window: Window? = dialog.getWindow()
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}