package com.example.countrypicker.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.countrypicker.enums.CountryFilter
import com.example.countrypicker.models.CountryDataClass
import com.example.countrypicker.utils.HelperFunctions

/***Function to display a custom country code picker
 * @author Shibin
 * @param phoneNumber  PhoneNumber state to be passed
 * @param default  the default country that needs to be shown on initial launch, US by default
 * @param filterType Need to select the filter type for your own purpose
 * @param filterCountries The filter list is applied based on the type specified
 * @param searchHint  Search hint to be displayed inside the bottomSheet
 * @param showFlag Whether to show flag in view or not, default is set to true
 * @param showName Whether to show country name in view or not, default is set to true
 * @param showCode Whether to show country code in view or not, default is set to false
 * @param showDialCode Whether to show dial code in view or not, default is set to false
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodePicker(
    default: String,
    phoneNumber: MutableState<String>,
    placeholder: String,
    filterType: CountryFilter = CountryFilter.ShowAllCountries,
    filterCountries: ArrayList<String> = arrayListOf(),
    searchHint: String,
    inputHint: String,
    lineColor: Color,
    showFlag: Boolean = true,
    showName: Boolean = true,
    showDialCode: Boolean = false,
    showCode: Boolean = false,
    bottomSheetHeight: Float,
    countryData: (CountryDataClass) -> Unit,
    updatedNumber: (String, String) -> Unit
) {
    val context = LocalContext.current

    val isFieldFocused = rememberSaveable { mutableStateOf(false) }
    val backgroundColor = Color.Transparent
    val transparent = Color.Transparent
    val borderColor = if (isFieldFocused.value) Color.Blue else Color.LightGray

    val textStyle = TextStyle()

    val countries = HelperFunctions.countriesFromLocal(context, filterType, filterCountries)
    val defaultCountry = HelperFunctions.getDefaultCountry(countries, default, "") {
        countryData(it)
    }

    val selectedCountry = remember { mutableStateOf(defaultCountry) }
    val isDialogShown = remember { mutableStateOf(false) }


    val colors = TextFieldDefaults.textFieldColors(
        containerColor = transparent,
        disabledIndicatorColor = transparent,
        disabledTextColor = textStyle.color,
        cursorColor = Color.Blue,
        focusedIndicatorColor = transparent,
        unfocusedIndicatorColor = transparent,
    )

    fun openSheet() {
        isFieldFocused.value = true
        isDialogShown.value = true
    }

    fun updateNumber(
        countryCode: String,
        number: String
    ) {
        updatedNumber(countryCode, number)
    }

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
            }
            updateNumber(
                selectedCountry.value.dialCode,
                phoneNumber.value
            )
        }
    }

    /*The view with textField to enter mobile number*/
    Box(
        modifier = Modifier
            .padding(10.dp)
            .drawWithContent {
                drawContent()
                drawLine(
                    color = borderColor ?: Color.LightGray,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
    ) {
        val focusManager = LocalFocusManager.current
        if (selectedCountry.value.image.isNullOrEmpty()) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier
                    .clickable { openSheet() }
                    .padding(5.dp)) {
                    Text(text = placeholder, style = textStyle)
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
                TextField(
                    placeholder = HelperFunctions.placeHolder(inputHint, textStyle),
                    modifier = Modifier.fillMaxWidth(),
                    value = phoneNumber.value,
                    colors = colors,
                    onValueChange = {
                        isFieldFocused.value = true
                        phoneNumber.value = it
                        updateNumber(selectedCountry.value.dialCode, it)
                    },
                    textStyle = textStyle,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                )
            }
        } else {
            TextField(
                placeholder = HelperFunctions.placeHolder(inputHint, textStyle),
                modifier = Modifier.fillMaxWidth(),
                value = phoneNumber.value,
                colors = colors,
                onValueChange = {
                    isFieldFocused.value = true
                    phoneNumber.value = it
                    updateNumber(selectedCountry.value.dialCode, it)
                },
                textStyle = textStyle,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { openSheet() }
                    ) {
                        if (showFlag) {
                            ShowFlag(context, selectedCountry.value.image)
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                        PickerTextView(
                            selectedCountry.value.dialCode,
                            style = textStyle,
                            isSelected = false
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                },
            )
        }
    }
}