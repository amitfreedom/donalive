package com.mobile.donalive.ui.login.models

data class VerifyPhoneEmailResponse(
    val message: String,
    val otp: Int,
    val success: String
)