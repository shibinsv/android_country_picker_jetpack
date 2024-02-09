package com.shibin.country_picker.utils

import android.content.Context
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import java.io.InputStream

object CommonUtils {

    @Composable
    fun isKeyboardVisible(): Boolean = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    fun loadFromLocalJson(context: Context, fileName: String): String {
        val inputStream: InputStream = context.assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        return String(buffer)
    }
}