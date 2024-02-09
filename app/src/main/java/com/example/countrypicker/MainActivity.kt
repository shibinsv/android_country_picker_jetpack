package com.example.countrypicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shibin.country_picker.enums.CountryFilter
import com.shibin.country_picker.models.CountryDataClass
import com.shibin.country_picker.views.CountryCodePicker
import com.shibin.country_picker.views.CountryPicker

class MainActivity : ComponentActivity() {

    companion object {
        const val bottomSheetHeight = 0.95f
        const val placeHolder = "Choose country"
        const val searchHint = "Search for a country"
        const val inputHint = "Enter phone number"
        const val default = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val phoneNumber = remember { mutableStateOf("") }
            val selectedCountry = remember { mutableStateOf(CountryDataClass("", "", "")) }
            Surface {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    CountryPicker(
                        filterType = CountryFilter.ShowSelectedCountries,
                        filterCountries = arrayListOf("IN", "AE"),
                        default = default,
                        placeholder = placeHolder,
                        searchHint = searchHint,
                        lineColor = Color.Blue,
                        onSelection = {
                            selectedCountry.value = it
                        },
                        bottomSheetHeight = bottomSheetHeight,
                        countryData = {
                            selectedCountry.value = it
                        }
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    CountryCodePicker(
                        default = default,
                        phoneNumber = phoneNumber,
                        placeholder = placeHolder,
                        searchHint = searchHint,
                        inputHint = inputHint,
                        lineColor = Color.Blue,
                        showDialCode = true,
                        bottomSheetHeight = bottomSheetHeight,
                        countryData = {
                            selectedCountry.value = it
                        },
                        updatedNumber = { code, number ->
                            val fullNumber = "$code $number"
                            println(fullNumber)
                            phoneNumber.value = number
                        }
                    )
                }
            }
        }
    }
}
