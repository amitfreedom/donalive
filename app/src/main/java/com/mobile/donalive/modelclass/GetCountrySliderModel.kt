package com.mobile.donalive.modelclass

data class GetCountrySliderModel(
    val details: List<CountryDetail>,
    val events: List<Event>,
    val message: String,
    val success: String
)

data class CountryDetail(
    val country_name: String
)

data class Event(
    val created: String,
    val id: String,
    val image: String,
    val updated: String
)