package com.shibin.country_picker.views

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.shibin.country_picker.models.CountryDataClass
import kotlinx.coroutines.launch
import kotlin.math.abs

/***
 * Function to popup BottomSheet
 * @author Shibin
 * @param context Context object
 * @param isDialogShown State value to be passed to handle dismiss
 * @param totalCountries List of all the countries to be populated in the view
 * @param currentSelection The present selected country
 * @param searchHint  Search hint to be displayed inside the bottomSheet
 * @param showFlag Whether to show flag in view or not, default is set to true
 * @param showName Whether to show country name in view or not, default is set to true
 * @param showCode Whether to show country code in view or not, default is set to false
 * @param showDialCode Whether to show dial code in view or not, default is set to false
 * @param showAlphabetScroll Show alphabetical scrollbar at the end of the view
 * @param onSelection Callback function to pass the updated country selected to the view
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowBottomSheet(
    context: Context,
    isDialogShown: MutableState<Boolean>,
    totalCountries: List<CountryDataClass?>,
    currentSelection: MutableState<CountryDataClass>,
    textStyle: TextStyle,
    lineColor: Color,
    searchHint: String,
    showFlag: Boolean,
    showName: Boolean,
    showCode: Boolean,
    showDialCode: Boolean,
    showAlphabetScroll: Boolean = true,
    bottomSheetHeight: Float,
    onSelection: (CountryDataClass?) -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentAlphabet = ""
    var searchText by remember { mutableStateOf("") }
    val currentList = remember { mutableStateOf(totalCountries) }

    /*For alphabetical scrollbar integration*/
    val listState = rememberLazyListState()
    val offsets = remember { mutableStateMapOf<Int, Float>() }
    var selectedHeaderIndex by remember { mutableStateOf(0) }
    var highlighted by remember { mutableStateOf("") }
    val headers =
        remember { currentList.value.map { it?.name?.first()?.uppercase() }.toSet().toList() }

    fun updateSelectedIndexIfNeeded(offset: Float) {
        val index =
            offsets.mapValues { abs(it.value - offset) }.entries.minByOrNull { it.value }?.key
                ?: return
        if (selectedHeaderIndex == index) return
        selectedHeaderIndex = index
        scope.launch {
            try {
                val firstAlphaIndex = currentList.value.indexOfFirst { e ->
                    e?.name?.first().toString() == headers[selectedHeaderIndex]
                }
                val scrollIndex = firstAlphaIndex + selectedHeaderIndex + 1
                listState.scrollToItem(scrollIndex)
            } catch (e: Exception) {
                print(e.message.toString())
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        shape = RectangleShape,
        scrimColor = Color.Transparent,
        sheetState = sheetState,
        onDismissRequest = { isDialogShown.value = false },
        dragHandle = null,
    ) {

        BoxWithConstraints {
//            val percent = if (isKeyboardVisible()) 0.88f else 0.92f
            val percent = bottomSheetHeight
            val sheetHeight = with(LocalDensity.current) { maxHeight * percent }

            Box(Modifier.height(sheetHeight)) {
                Box {
                    Column {
                        TextField(modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                cursorColor = Color.Blue,
                                focusedIndicatorColor = lineColor,
                                unfocusedIndicatorColor = lineColor,
                            ),
                            textStyle = textStyle,
                            value = searchText,
                            onValueChange = {
                                searchText = it.lowercase().trim()
                                searchText = it.lowercase().trim()
                                if (searchText.isEmpty()) {
                                    currentList.value = totalCountries
                                } else {
                                    val queryList = totalCountries.filter { country ->
                                        listOf(
                                            country?.name ?: "",
                                            country?.code ?: "",
                                            country?.dialCode ?: ""
                                        ).checkValueInList(searchText)
                                    }
                                    currentList.value = queryList
                                }
                            },
                            placeholder = {
                                PickerTextView(
                                    text = searchHint,
                                    style = textStyle,
                                    isSelected = false
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                if (searchText.isNotEmpty()) {
                                    Icon(
                                        modifier = Modifier.clickable {
                                            searchText = ""
                                            currentList.value = totalCountries
                                        },
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null
                                    )
                                }
                            })
                        Row(
                            Modifier.background(Color.LightGray.copy(alpha = 0.3f))
                        ) {
                            LazyColumn(state = listState) {
                                try {
                                    highlighted =
                                        currentList.value[listState.firstVisibleItemIndex]?.name?.first()
                                            .toString()
                                } catch (e: Exception) {
                                    print(e)
                                }
                                /*Show the selected item when the search text is empty*/
                                if (searchText.isEmpty() && !currentSelection.value.image.isNullOrEmpty()) {
                                    item {
                                        Box(Modifier.clickable {
                                            onSelection(currentSelection.value)
                                        }
                                        ) {
                                            CountryItem(
                                                context,
                                                currentSelection.value,
                                                textStyle,
                                                showFlag,
                                                showName,
                                                showCode,
                                                showDialCode,
                                                isSelected = true
                                            )
                                        }
                                    }
                                }
                                currentList.value.forEach { itemData ->
                                    val firstLetter = itemData?.name?.first().toString()
                                    if (firstLetter != currentAlphabet) {
                                        currentAlphabet = firstLetter
                                        item {
                                            Box(Modifier.padding(20.dp)) {
                                                Text(text = firstLetter, style = textStyle)
                                            }
                                        }
                                    }
                                    item {
                                        if (itemData != null) {
                                            Box(Modifier.clickable { onSelection(itemData) }) {
                                                CountryItem(
                                                    context,
                                                    itemData,
                                                    textStyle,
                                                    showFlag,
                                                    showName,
                                                    showCode,
                                                    showDialCode
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (showAlphabetScroll) {
                    AlphabeticalScrollBar(
                        headers,
                        highlighted,
                        offsets
                    ) { updateSelectedIndexIfNeeded(it) }
                }
            }
        }
    }
}
