package com.mobile.donalive.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.donalive.R
import com.mobile.donalive.databinding.ActivityFriendsFollowersFollowingBinding
import com.mobile.donalive.databinding.ActivityLiveHistoryBinding
import com.mobile.donalive.ui.profile.adapters.LiveHistoryAdapter

class FriendsFollowersFollowingActivity : AppCompatActivity() {
    private var _binding: ActivityFriendsFollowersFollowingBinding?=null
    private val binding get() = _binding!!
    private var title:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFriendsFollowersFollowingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title=intent.getStringExtra("appBarTitle").toString()
        binding.txtTitle.text=title

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}