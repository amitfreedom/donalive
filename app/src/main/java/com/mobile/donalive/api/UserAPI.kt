package com.mobile.donalive.api

import com.mobile.donalive.modelclass.LiveMultiliveTokemModel
import com.mobile.donalive.models.UserRequest
import com.mobile.donalive.models.UserResponse
import com.mobile.donalive.ui.login.models.VerifyPhoneEmailRequest
import com.mobile.donalive.ui.login.models.VerifyPhoneEmailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserAPI {
    @POST("/users/signup")
    suspend fun signup(@Body userRequest: UserRequest) : Response<UserResponse>

    @POST("/users/signin")
    suspend fun signin(@Body userRequest: UserRequest) : Response<UserResponse>

    @POST("verify-phone-email")
    suspend fun verifyPhoneEmail(@Body verifyPhoneEmail:VerifyPhoneEmailRequest): Response<VerifyPhoneEmailResponse>


}