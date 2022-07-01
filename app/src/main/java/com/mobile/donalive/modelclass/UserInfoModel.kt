package com.mobile.donalive.modelclass

data class UserInfoModel(
    val details: Details,
    val message: String,
    val status: String,
    val success: String
)

data class Details(
    val bio: String,
    val category: String,
    val country_code: String,
    val country_name: String,
    val created: String,
    val device_id: String,
    val dimaond: String,
    val dob: String,
    val email: String,
    val freeMatches: String,
    val gender: String,
    val id: String,
    val image: String,
    val latitude: String,
    val likes: String,
    val likedBy: String,
    val longitude: String,
    val member_status: String,
    val name: String,
    val online_status: String,
    val otp: String,
    val phone: String,
    val reg_id: String,
    val registerType: String,
    val social_id: String,
    val stage: String,
    val status: String,
    val updated: String,
    val username: String,
    val views: String
)



