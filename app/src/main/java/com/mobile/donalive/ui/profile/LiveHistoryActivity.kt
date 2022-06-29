package com.mobile.donalive.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.donalive.R
import com.mobile.donalive.databinding.ActivityHomeBinding
import com.mobile.donalive.databinding.ActivityLiveHistoryBinding
import com.mobile.donalive.ui.profile.adapters.LiveHistoryAdapter
import com.mobile.donalive.ui.profile.adapters.MyLevelAdapter

class LiveHistoryActivity : AppCompatActivity() {
    private var _binding: ActivityLiveHistoryBinding?=null
    private val binding get() = _binding!!
    private lateinit var adapter: LiveHistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLiveHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAdapter()
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setAdapter() {
        adapter = LiveHistoryAdapter()
        binding.rvLiveHistory.adapter = adapter
    }
}