package com.mobile.donalive.modelclass

import java.io.Serializable

data class OtherUserDetailsModel(
    val details: OtherUserDetails,
    val message: String,
    val success: String
) : Serializable

data class OtherUserDetails(
    val country_name: String,
    val dob: String,
    val gender: String,
    val id: String,
    val image: String,
    val images: List<Image>,
    val videos: List<Videos>,
    val name: String,
    val likeStatus: String,
    val username: String
) : Serializable

data class Image(
    val id: String,
    val likes: String,
    val mediaPath: String,
    val mediaType: String,
    val modeType: String,
    val userId: String,
    val views: String
) : Serializable


data class Videos(
    val id: String,
    val likes: String,
    val mediaPath: String,
    val mediaType: String,
    val modeType: String,
    val userId: String,
    val views: String
) : Serializable






