package com.mobile.donalive.ui.toplist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.donalive.databinding.ActivityLiveHistoryBinding
import com.mobile.donalive.databinding.ActivityTopListBinding
import com.mobile.donalive.ui.profile.adapters.LiveHistoryAdapter

class TopListActivity : AppCompatActivity() {
    private var _binding: ActivityTopListBinding?=null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}