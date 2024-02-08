package com.example.countrypicker.models

data class CountryDataClass(
    val name: String,
    val dialCode: String,
    val code: String,
    val image: String? = null
)