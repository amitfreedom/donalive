package com.mobile.donalive.ui.home.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobile.donalive.R
import com.mobile.donalive.databinding.FragmentChatBinding
import com.mobile.donalive.databinding.FragmentProfileBinding
import com.mobile.donalive.ui.home.activity.HomeActivity
import com.mobile.donalive.ui.profile.*
import com.mobile.donalive.ui.settings.SettingActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnHostRegister.setOnClickListener{
            val mainIntent = Intent(activity, HostRegistrationFormActivity::class.java)
            startActivity(mainIntent)
        }

        binding.btnLevel.setOnClickListener{
            val mainIntent = Intent(activity, MyLevelActivity::class.java)
            startActivity(mainIntent)
        }
        binding.btnLiveHistory.setOnClickListener{
            val mainIntent = Intent(activity, LiveHistoryActivity::class.java)
            startActivity(mainIntent)
        }

        binding.btnFriends.setOnClickListener {
            moveToNext("Friends List")
        }
        binding.btnFollowers.setOnClickListener {
            moveToNext("Followers")
        }
        binding.btnFollowing.setOnClickListener {
            moveToNext("Following")
        }
        binding.btnEdit.setOnClickListener {
            val mainIntent = Intent(activity, EditProfileActivity::class.java)
            startActivity(mainIntent)
        }
        binding.btnSettings.setOnClickListener {
            val mainIntent = Intent(activity, SettingActivity::class.java)
            startActivity(mainIntent)
        }

        binding.btnTopUp.setOnClickListener {
            val mainIntent = Intent(activity, TopUpActivity::class.java)
            startActivity(mainIntent)
        }
        binding.btnBalance.setOnClickListener {
            val mainIntent = Intent(activity, BalanceActivity::class.java)
            startActivity(mainIntent)
        }
    }

    private fun moveToNext(title: String) {
        val mainIntent = Intent(activity, FriendsFollowersFollowingActivity::class.java)
        mainIntent.putExtra("appBarTitle",title)
        startActivity(mainIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}