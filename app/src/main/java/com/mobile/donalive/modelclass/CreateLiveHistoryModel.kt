package com.mobile.donalive.modelclass

data class CreateLiveHistoryModel(
    val details: CreateLiveHistoryDetails,
    val message: String,
    val success: String
)

data class CreateLiveHistoryDetails(
    val created: String,
    val endLive: String,
    val id: String,
    val liveType: String,
    val startLive: String,
    val updated: String,
    val userId: String
)