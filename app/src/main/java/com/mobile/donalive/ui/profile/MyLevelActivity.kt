package com.mobile.donalive.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.donalive.R
import com.mobile.donalive.databinding.ActivityHomeBinding
import com.mobile.donalive.databinding.ActivityMyLevelBinding
import com.mobile.donalive.ui.home.adapters.ChatAdapter
import com.mobile.donalive.ui.profile.adapters.MyLevelAdapter

class MyLevelActivity : AppCompatActivity() {
    private var _binding: ActivityMyLevelBinding?=null
    private val binding get() = _binding!!
    private lateinit var adapter: MyLevelAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityMyLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupLevel()
        clicks()
    }

    private fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupLevel() {
        adapter = MyLevelAdapter()
        binding.recyclerView.adapter = adapter
    }
}