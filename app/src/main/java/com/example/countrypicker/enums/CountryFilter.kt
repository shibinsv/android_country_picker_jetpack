package com.example.countrypicker.enums

enum class CountryFilter {
    ShowAllCountries, //To show all the countries from the available json
    Provided, //To show the countries provided from the user
    ShowSelectedCountries, // To show only the countries whose code is provided
    RestrictCountries // To show countries restricting the provided ones
}