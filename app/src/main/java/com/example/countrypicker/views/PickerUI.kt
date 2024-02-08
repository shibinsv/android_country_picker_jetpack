package com.example.countrypicker.views

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.countrypicker.models.CountryDataClass


@Composable
fun PickerTextView(text: String, style: TextStyle, isSelected: Boolean, spacing: Int = 10) {
    var textStyle = style
    if (isSelected) {
        textStyle = style.copy(color = Color.Blue)
    }
    Row {
        Spacer(modifier = Modifier.width(spacing.dp))
        Text(text = text, style = textStyle)
    }
}


@Composable
fun PickerContent(
    value: CountryDataClass,
    style: TextStyle,
    showName: Boolean,
    showDialCode: Boolean,
    isSelected: Boolean = false
) {
    Row {
        if (showName) PickerTextView(text = value.name, style, isSelected)
        if (showDialCode) PickerTextView(
            text = "(${value.dialCode})",
            style,
            isSelected,
            spacing = 5
        )
    }
}

@Composable
fun CountryItem(
    context: Context,
    itemData: CountryDataClass,
    textStyle: TextStyle,
    showFlag: Boolean,
    showName: Boolean,
    showCode: Boolean,
    showDialCode: Boolean,
    isSelected: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        itemData.apply {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showFlag) ShowFlag(context = context, image = image)
                    PickerContent(
                        itemData, textStyle, showName, showDialCode, isSelected
                    )
                }
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 10.dp),
                        tint = Color.Blue
                    )
                }
            }
        }
    }
}


@Composable
fun ShowFlag(context: Context, image: String?, spacing: Int = 20) {
    val model = ImageRequest.Builder(context)
        .decoderFactory(SvgDecoder.Factory())
        .data(image)
        .build()
    Row {
        Spacer(modifier = Modifier.width(spacing.dp))
        AsyncImage(
            modifier = Modifier.size(35.dp),
            model = model,
            contentDescription = null
        )
    }
}


/**
 * Function to display an alphabetical scroll bar
 * @author Shibin
 * @param headers List of alphabets
 * @param highlighted current highlighted letter in the scrollbar
 * @param updateSelection Update selection based on the click action
 * */
@Composable
fun AlphabeticalScrollBar(
    headers: List<String?>,
    highlighted: String,
    offsets: SnapshotStateMap<Int, Float>,
    updateSelection: (Float) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
        Column(verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures {
                        updateSelection(it.y)
                    }
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, _ ->
                        updateSelection(change.position.y)
                    }
                }) {
            headers.forEachIndexed { i, header ->
                val textColor = Color.Black
                val fontSize = 12.sp
                if (header != null) {
                    Text(header,
                        color = textColor,
                        fontSize = fontSize,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .onGloballyPositioned {
                                offsets[i] = it.boundsInParent().center.y
                            }
                    )
                }
            }
        }
    }
}