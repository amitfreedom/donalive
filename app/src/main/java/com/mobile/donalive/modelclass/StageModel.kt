package com.mobile.donalive.modelclass

data class StageModel(
    val details: StageDetails,
    val message: String,
    val success: String
)

data class StageDetails(
    val id: String,
    val stage: String
)