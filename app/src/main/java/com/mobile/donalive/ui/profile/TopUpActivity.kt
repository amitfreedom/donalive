package com.mobile.donalive.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.donalive.R
import com.mobile.donalive.databinding.ActivityTopListBinding
import com.mobile.donalive.databinding.ActivityTopUpBinding

class TopUpActivity : AppCompatActivity() {
    private var _binding:ActivityTopUpBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivityTopUpBinding.inflate(layoutInflater)
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