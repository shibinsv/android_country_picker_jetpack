package com.example.countrypicker.utils

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.countrypicker.enums.CountryFilter
import com.example.countrypicker.models.CountryDataClass
import com.example.countrypicker.utils.CommonUtils.loadFromLocalJson
import com.google.gson.Gson

object HelperFunctions {

    fun placeHolder(hint: String, style: TextStyle): @Composable() (() -> Unit) {
        return { Text(text = hint, style = TextStyle(color = Color.LightGray)) }
    }

    /** Function to get the list of all countries available from the json*/
    fun countriesFromLocal(
        context: Context,
        filterType: CountryFilter,
        filterCountries: ArrayList<String>
    ): List<CountryDataClass?> {
        var countries = listOf<CountryDataClass>()
        try {
            val jsonString = loadFromLocalJson(context, "country.json")
            countries = Gson().fromJson(jsonString, Array<CountryDataClass>::class.java)
                .sortedBy { e -> e.name }.toList()
        } catch (e: Exception) {
            print(e.message)
        }
        when (filterType) {
            CountryFilter.ShowAllCountries -> {
                /*Do nothing*/
                return  countries
            }

            CountryFilter.Provided -> {
                /*Need to get the countries only when it is in the same data class structure*/
                return  countries
            }
            CountryFilter.ShowSelectedCountries -> {
                val availableCountries = mutableListOf<CountryDataClass?>()
                filterCountries.forEach {country ->
                   val list = countries.filter { it.code == country }
                    availableCountries.addAll(list)
                }

                return  availableCountries
            }
            CountryFilter.RestrictCountries -> {
                val availableCountries = mutableListOf<CountryDataClass?>()
                filterCountries.forEach {country ->
                    val list = countries.filterNot {
                        it.code == country
                    }
                    availableCountries.addAll(list)
                }

                return  availableCountries
            }
        }
    }

    /** Function to get the default country */
    fun getDefaultCountry(
        countries: List<CountryDataClass?>,
        defaultCountry: String,
        hint: String,
        onSelection: (CountryDataClass) -> Unit
    ): CountryDataClass {
        val default = if (defaultCountry.isEmpty()) {
            CountryDataClass(
                hint,
                "",
                "",
                null
            )
        } else {
            countries.find { e -> e?.code == defaultCountry } ?: CountryDataClass(
                "United States",
                "+1",
                "US",
                "https://cdn.jsdelivr.net/npm/country-flag-emoji-json@2.0.0/dist/images/US.svg"
            )
        }
        onSelection(default)
        return default
    }
}
