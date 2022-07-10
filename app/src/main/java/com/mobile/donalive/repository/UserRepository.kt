package com.mobile.donalive.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.donalive.api.UserAPI
import com.mobile.donalive.modelclass.LiveMultiliveTokemModel
import com.mobile.donalive.models.UserRequest
import com.mobile.donalive.models.UserResponse
import com.mobile.donalive.ui.login.models.VerifyPhoneEmailRequest
import com.mobile.donalive.ui.login.models.VerifyPhoneEmailResponse
import com.mobile.donalive.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val userAPI: UserAPI) {

    private val _userResponseLiveData = MutableLiveData<NetworkResult<UserResponse>>()
    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = _userResponseLiveData

    suspend fun registerUser(userRequest: UserRequest) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response = userAPI.signup(userRequest)
        handleResponse(response)
    }

    suspend fun loginUser(userRequest: UserRequest) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response = userAPI.signin(userRequest)
        handleResponse(response)
    }

    private fun handleResponse(response: Response<UserResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _userResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _userResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _userResponseLiveData.postValue(NetworkResult.Error("something went wrong"))
        }
    }
    private val _verifyPhoneEmailResponseLiveData = MutableLiveData<NetworkResult<VerifyPhoneEmailResponse>>()
    val verifyPhoneEmailResponseLiveData: LiveData<NetworkResult<VerifyPhoneEmailResponse>>
        get() = _verifyPhoneEmailResponseLiveData


    suspend fun verifyPhoneEmail(verifyPhoneEmail: VerifyPhoneEmailRequest) {
        _verifyPhoneEmailResponseLiveData.postValue(NetworkResult.Loading())
        val response = userAPI.verifyPhoneEmail(verifyPhoneEmail)
        handleVerifyPhoneEmailResponse(response)

    }

    private fun handleVerifyPhoneEmailResponse(response: Response<VerifyPhoneEmailResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _verifyPhoneEmailResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _verifyPhoneEmailResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _verifyPhoneEmailResponseLiveData.postValue(NetworkResult.Error("something went wrong"))
        }
    }

}