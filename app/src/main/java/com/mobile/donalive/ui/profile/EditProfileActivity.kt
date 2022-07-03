package com.mobile.donalive.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.mobile.donalive.databinding.ActivityEditProfileBinding
import com.mobile.donalive.databinding.ActivityLiveHistoryBinding
import com.mobile.donalive.ui.profile.adapters.LiveHistoryAdapter

class EditProfileActivity : AppCompatActivity() {
    private var _binding: ActivityEditProfileBinding?=null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnSave.setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}