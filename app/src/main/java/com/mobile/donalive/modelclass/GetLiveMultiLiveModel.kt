package com.mobile.donalive.modelclass

data class GetLiveMultiLiveModel(
    val details: List<GetLiveMultiLiveDetail>,
    val message: String,
    val success: String
)

data class GetLiveMultiLiveDetail(
    val channelName: String,
    val countFollwers: String,
    val created: String,
    val dimaond: String,
    val id: String,
    val image: String,
    val liveID: String,
    val liveStatus: String,
    val liveType: String,
    val name: String,
    val token: String,
    val updated: String,
    val userId: String
)
