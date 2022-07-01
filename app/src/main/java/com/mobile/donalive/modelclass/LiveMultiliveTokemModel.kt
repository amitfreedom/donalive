package com.mobile.donalive.modelclass

import java.io.Serializable

data class LiveMultiliveTokemModel(
    val message: String,
    val success: String,
    val token: Token
) : Serializable

data class Token(
    val channelName: String,
    val created: String,
    val liveStatus: String,
    val liveType: String,
    val token: String,
    val userId: String,
    val image: String,
    val name: String,
    val count: String,
) : Serializable