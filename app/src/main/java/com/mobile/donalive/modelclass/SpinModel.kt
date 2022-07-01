package com.mobile.donalive.modelclass

data class SpinModel(
    val gender: List<Gender>,
    val message: String,
    val nearBy: List<NearBy>,
    val purposeList: List<Purpose>,
    val success: String
)

data class Gender(
    val gender: String,
    val id: String,
    val image: String,
    val latitude: String,
    val longitude: String,
    val name: String
)

data class NearBy(
    val distance: String,
    val gender: String,
    val id: String,
    val image: String,
    val latitude: String,
    val longitude: String,
    val name: String
)

data class Purpose(
    val gender: String,
    val id: String,
    val image: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val purposeId: String,
    val userId: String
)