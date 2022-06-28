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
import com.mobile.donalive.ui.profile.HostRegistrationFormActivity

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}