package com.mobile.donalive.modelclass

data class HomeModel(
    val details: List<HomeDetails>,
    val message: String,
    val success: String,
    val users: Users
)

data class HomeDetails(
    val distance: String,
    val gender: String,
    val id: String,
    val image: String,
    val latitude: String,
    val longitude: String
)

data class Users(
    val dimaond: String,
    val freeMatches: String,
    val id: String,
    val member_status: String
)