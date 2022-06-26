package com.mobile.donalive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mobile.donalive.databinding.FragmentRegisterBinding
import com.mobile.donalive.models.UserRequest
import com.mobile.donalive.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() =_binding!!
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_otpVerificationFragment)
//            val validateResult=validateUserInput()
//            if (validateResult.first){
//                authViewModel.registerUser(getUserRequest())
//            }else{
//                binding.txtError.text=validateResult.second
//            }


        }
        binding.btnLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        bindObserve()
    }

    private fun getUserRequest(): UserRequest {
        val emailAddress=binding.txtEmail.text.toString().trim()
        val password=binding.txtPassword.text.toString().trim()
        val username=binding.txtUsername.text.toString().trim()
        return UserRequest(emailAddress,password, username)
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val userRequest=getUserRequest()
        return authViewModel.validateCredentials(userRequest.email,userRequest.username,userRequest.password,false)
    }

    private fun bindObserve() {
        authViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible=false
            when(it){
                is NetworkResult.Success ->{
//                    findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
                }
                is NetworkResult.Error ->{
                    binding.txtError.text=it.message
                }
                is NetworkResult.Loading ->{
                    binding.progressBar.isVisible=true
                }
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}