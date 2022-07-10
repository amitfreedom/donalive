package com.mobile.donalive.ui.login.viewmodel

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.donalive.modelclass.LiveMultiliveTokemModel
import com.mobile.donalive.models.UserRequest
import com.mobile.donalive.models.UserResponse
import com.mobile.donalive.ui.login.models.VerifyPhoneEmailResponse
import com.mobile.donalive.repository.UserRepository
import com.mobile.donalive.ui.login.models.VerifyPhoneEmailRequest
import com.mobile.donalive.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository):ViewModel() {

    val userResponseLiveData : LiveData<NetworkResult<UserResponse>>
    get() = userRepository.userResponseLiveData

    fun registerUser(userRequest: UserRequest){
        viewModelScope.launch {
            userRepository.registerUser(userRequest)
        }
    }

    fun loginUser(userRequest: UserRequest){
        viewModelScope.launch {
            userRepository.loginUser(userRequest)
        }
    }

    // verifyPhoneEmail

    val verifyPhoneEmailResponseLiveData : LiveData<NetworkResult<VerifyPhoneEmailResponse>>
        get() = userRepository.verifyPhoneEmailResponseLiveData

    fun verifyPhoneEmail(verifyPhoneEmail: VerifyPhoneEmailRequest){
        viewModelScope.launch {
            userRepository.verifyPhoneEmail(verifyPhoneEmail)
        }
    }

}