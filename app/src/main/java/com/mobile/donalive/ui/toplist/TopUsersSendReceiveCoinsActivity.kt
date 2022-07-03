package com.mobile.donalive.ui.toplist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.donalive.databinding.ActivityTopUpBinding
import com.mobile.donalive.databinding.ActivityTopUsersSendReceiveCoinsBinding

class TopUsersSendReceiveCoinsActivity : AppCompatActivity() {
    private var _binding: ActivityTopUsersSendReceiveCoinsBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityTopUsersSendReceiveCoinsBinding.inflate(layoutInflater)
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