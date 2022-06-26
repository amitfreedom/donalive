package com.mobile.donalive.ui.starter

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.mobile.donalive.MainActivity
import com.mobile.donalive.R
import com.mobile.donalive.databinding.FragmentStarterBinding
import com.mobile.donalive.ui.home.activity.HomeActivity
import com.mobile.donalive.utils.global.CommonMethod

class StarterFragment : Fragment() {
    private var _binding:FragmentStarterBinding?=null
    private val binding get() = _binding!!
    private var isView:Boolean =true
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
            val mainIntent = Intent(activity, HomeActivity::class.java)
            startActivity(mainIntent)
        }
        binding.btnWhatsapp.setOnClickListener{
            CommonMethod.showMessage(activity,"coming soon...")
        }
        binding.btnPhone.setOnClickListener{
           findNavController().navigate(R.id.action_starterFragment_to_phoneFragment)
        }
        binding.btnFacebook.setOnClickListener{
            CommonMethod.showMessage(activity,"coming soon...")
        }
        binding.btnViewMore.setOnClickListener{
            binding.btnViewMore.visibility=View.GONE
            binding.btnMoreButton.visibility=View.VISIBLE
        }
        binding.btnTermCondition.setOnClickListener{
            CommonMethod.showMessage(activity,"coming soon...")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}