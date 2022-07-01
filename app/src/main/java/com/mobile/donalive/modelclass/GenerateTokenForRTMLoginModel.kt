package com.mobile.donalive.modelclass

data class GenerateTokenForRTMLoginModel(
    val channelName: String,
    val message: String,
    val success: String,
    val token: String
)