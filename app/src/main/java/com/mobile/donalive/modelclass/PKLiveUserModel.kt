package com.mobile.donalive.modelclass

data class PKLiveUserModel(
    val dimaond: List<Dimaond>,
    val message: String,
    val success: String
)

data class Dimaond(
    val channelName: String,
    val created: String,
    val endLive: String,
    val id: String,
    val image: String,
    val liveType: String,
    val live_status: String,
    val name: String,
    val startLive: String,
    val token: String,
    val updated: String,
    val userId: String
)