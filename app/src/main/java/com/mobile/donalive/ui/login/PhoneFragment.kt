package com.mobile.donalive.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mobile.donalive.R
import com.mobile.donalive.databinding.FragmentPhoneBinding

class PhoneFragment : Fragment() {
    private var _binding:FragmentPhoneBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPhoneBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_phoneFragment_to_otpVerificationFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}