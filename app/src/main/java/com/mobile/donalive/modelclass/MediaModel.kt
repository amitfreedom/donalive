package com.mobile.donalive.modelclass

import java.io.Serializable

data class MediaModel(
    val details: List<MediaList>,
    val message: String,
    val success: String
):Serializable

data class MediaList(
    val created: String,
    val id: String,
    val likes: String,
    val mediaPath: String,
    val mediaType: String,
    val modeType: String,
    val updated: String,
    val userId: String,
    val views: String
) :Serializable