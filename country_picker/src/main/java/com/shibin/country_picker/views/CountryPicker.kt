package com.shibin.country_picker.views


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.shibin.country_picker.enums.CountryFilter
import com.shibin.country_picker.models.CountryDataClass
import com.shibin.country_picker.utils.HelperFunctions

/**Function to display a custom country picker
 * @author Shibin
 * @param default  the default country that needs to be shown on initial launch, US by default
 * @param filterType Need to select the filter type for your own purpose
 * @param filterCountries The filter list is applied based on the type specified
 * @param searchHint  Search hint to be displayed inside the bottomSheet
 * @param showFlag Whether to show flag in view or not, default is set to true
 * @param showName Whether to show country name in view or not, default is set to true
 * @param showCode Whether to show country code in view or not, default is set to false
 * @param showDialCode Whether to show dial code in view or not, default is set to false
 * @param onSelection Callback function to pass the updated country selected to the view
 * */
@Composable
fun CountryPicker(
    default: String,
    placeholder: String,
    searchHint: String,
    lineColor: Color,
    filterType: CountryFilter = CountryFilter.ShowAllCountries,
    filterCountries: ArrayList<String> = arrayListOf(),
    showFlag: Boolean = true,
    showName: Boolean = true,
    showDialCode: Boolean = false,
    showCode: Boolean = false,
    bottomSheetHeight: Float,
    onSelection: (CountryDataClass) -> Unit,
    countryData: (CountryDataClass) -> Unit
) {
    val context = LocalContext.current
    val isFieldFocused = rememberSaveable { mutableStateOf(false) }
    val textStyle = TextStyle()
    val countries = HelperFunctions.countriesFromLocal(context, filterType, filterCountries)
    val defaultCountry = HelperFunctions.getDefaultCountry(countries, default, placeholder) {
        countryData(it)
    }

    val selectedCountry = remember { mutableStateOf(defaultCountry) }
    val isDialogShown = remember { mutableStateOf(false) }

    if (isDialogShown.value) {
        ShowBottomSheet(
            context,
            isDialogShown,
            countries,
            selectedCountry,
            textStyle,
            lineColor,
            searchHint,
            showFlag,
            showName,
            showCode,
            showDialCode,
            showAlphabetScroll = true,
            bottomSheetHeight
        ) {
            isDialogShown.value = false
            it?.let { country ->
                selectedCountry.value = country
                onSelection(country)
            }
        }
    }

    /*View to be shown default*/
    Box(modifier = Modifier
        .padding(10.dp)
        .drawWithContent {
            drawContent()
            drawLine(
                color = lineColor,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2.dp.toPx()
            )
        }
        .clickable {
            isFieldFocused.value = true
            isDialogShown.value = true
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showFlag && selectedCountry.value.image?.isNotEmpty() == true) {
                    ShowFlag(context, selectedCountry.value.image, 0)
                }
                PickerContent(
                    selectedCountry.value, textStyle, showName, showCode, showDialCode
                )
            }
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
        }
    }
}


fun List<String>.checkValueInList(text: String): Boolean {
    val boolList = mutableListOf<Boolean>()
    this.forEach {
        val containValue = it.lowercase().contains(text)
        boolList.add(containValue)
    }
    return boolList.contains(true)
}
