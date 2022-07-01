package com.mobile.donalive.api

import com.mobile.donalive.modelclass.LiveMultiliveTokemModel
import com.mobile.donalive.models.UserRequest
import com.mobile.donalive.models.UserResponse
import retrofit2.Call
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


    @FormUrlEncoded
    @POST("live-Multi-Live-Token")
    suspend fun liveMultiLiveAgora(@FieldMap data: HashMap<String, String>): Call<LiveMultiliveTokemModel>
}