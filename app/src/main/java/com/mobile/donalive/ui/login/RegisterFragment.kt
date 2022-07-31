package com.mobile.donalive.ui.login

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mobile.donalive.R
import com.mobile.donalive.ui.login.viewmodel.AuthViewModel
import com.mobile.donalive.databinding.FragmentRegisterBinding
import com.mobile.donalive.ui.login.models.VerifyPhoneEmailRequest
import com.mobile.donalive.utils.Helper.Companion.validateCredentials
import com.mobile.donalive.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
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
//            findNavController().navigate(R.id.action_registerFragment_to_otpVerificationFragment)
            val emailAddress = binding.txtEmail.text.toString().trim()
            val phone = binding.txtPhone.text.toString().trim()
            val username = binding.txtUsername.text.toString().trim()
            val password = binding.txtPassword.text.toString().trim()

            if (username.isEmpty()) {
                binding.txtError.text = "Please enter username."
            }
            else if (emailAddress.isEmpty()) {
                binding.txtError.text = "Email address can't be empty."
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                binding.txtError.text = "Please enter valid email address."
            } else if (phone.isEmpty()) {
                binding.txtError.text = "Please enter phone."
            } else if (username.isEmpty()) {
                binding.txtError.text = "Please enter username."
            } else if (password.isEmpty() && password.length <= 5) {
                binding.txtError.text = "Password length should be greater than 5"
            } else {
                binding.txtError.text = ""
                authViewModel.verifyPhoneEmail(getVerifyPhoneEmailRequest())
            }

        }
        binding.btnLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        bindObserve()
    }

    private fun getVerifyPhoneEmailRequest(): VerifyPhoneEmailRequest {
        val emailAddress = binding.txtEmail.text.toString().trim()
        val phone = binding.txtPhone.text.toString().trim()
        return VerifyPhoneEmailRequest(emailAddress, phone)
    }


    private fun bindObserve() {
        authViewModel.verifyPhoneEmailResponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    Toast.makeText(
                        activity,
                        "" + it.data?.message + "\n" + "otp : " + it.data?.otp,
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_registerFragment_to_otpVerificationFragment)
                }
                is NetworkResult.Error -> {
                    binding.txtError.text = it.message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}