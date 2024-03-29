package com.mobile.donalive.ui.home.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.mobile.donalive.R
import com.mobile.donalive.databinding.ActivityHomeBinding
import com.mobile.donalive.stream.ChannelFragment
import com.mobile.donalive.ui.home.fragments.ChatFragment
import com.mobile.donalive.ui.home.fragments.HomeFragment
import com.mobile.donalive.ui.home.fragments.MainLiveFragment
import com.mobile.donalive.ui.home.fragments.ProfileFragment
import com.mobile.donalive.ui.login.LoginFragment
import com.mobile.donalive.ui.login.RegisterFragment
import com.mobile.donalive.ui.otp.OtpVerificationFragment
import com.mobile.donalive.ui.settings.SettingsFragment
import com.mobile.donalive.utils.global.CommonMethod


class HomeActivity : AppCompatActivity() {
    private var _binding:ActivityHomeBinding?=null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.menu.getItem(0).isCheckable = true
        setFragment(HomeFragment())
        setUpNavigation()

    }

    private fun setUpNavigation() {
        binding.bottomNav.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when(item.itemId){
                R.id.action_home -> {
                    setFragment(HomeFragment())

                }
                R.id.action_chat -> {
                    setFragment(ChannelFragment())
//                    setFragment(ChatFragment())
                }
                R.id.action_user -> {
                    setFragment(ProfileFragment())
                }R.id.action_live -> {
                    setFragment(MainLiveFragment())
                }R.id.action_video -> {
                    setFragment(MainLiveFragment())
                }
            }
            true
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    fun setFragment(fr : Fragment){
        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.fragment,fr)
        frag.commit()
    }
}