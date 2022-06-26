package com.mobile.donalive.ui.starter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mobile.donalive.R
import com.mobile.donalive.databinding.FragmentStarterBinding

class StarterFragment : Fragment() {
    private var _binding:FragmentStarterBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStarterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEmail.setOnClickListener{
            findNavController().navigate(R.id.action_starterFragment_to_loginFragment)
        }
        binding.btnGoogle.setOnClickListener{

        }
        binding.btnPhone.setOnClickListener{

        }
        binding.btnTermCondition.setOnClickListener{

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}