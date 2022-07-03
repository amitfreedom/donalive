package com.mobile.donalive.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.donalive.R
import com.mobile.donalive.databinding.ActivityBalanceBinding
import com.mobile.donalive.databinding.ActivityTopUpBinding

class BalanceActivity : AppCompatActivity() {
    private var _binding: ActivityBalanceBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityBalanceBinding.inflate(layoutInflater)
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