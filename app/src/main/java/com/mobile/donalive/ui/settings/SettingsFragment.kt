package com.mobile.donalive.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mobile.donalive.R
import com.mobile.donalive.databinding.FragmentLoginBinding
import com.mobile.donalive.databinding.FragmentSettingsBinding
import com.mobile.donalive.ui.home.activity.HomeActivity


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() =_binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEnter.setOnClickListener {
            val mainIntent = Intent(activity, HomeActivity::class.java)
            startActivity(mainIntent)        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}