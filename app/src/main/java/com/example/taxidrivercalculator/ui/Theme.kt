package com.example.taxidrivercalculator.ui

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import com.example.taxidrivercalculator.R

@Composable
fun appColorScheme(isDarkTheme: Boolean): ColorScheme {
    val context = LocalContext.current
    return if (isDarkTheme) {
        darkColorScheme(
            primary = colorFromRes(context, R.color.yellow_3),
            onPrimary = colorFromRes(context, R.color.black),
            secondary = colorFromRes(context, R.color.yellow_3),
            onSecondary = colorFromRes(context, R.color.black),
            background = colorFromRes(context, R.color.black), // android:colorBackground
            surface = colorFromRes(context, R.color.dark_gray), // bottomNavigationBackground
            onBackground = colorFromRes(context, R.color.white), // android:textColor
            onSurface = colorFromRes(context, R.color.hint_night) // android:textColorHint
        )
    } else {
        lightColorScheme(
            primary = colorFromRes(context, R.color.yellow_4),
            onPrimary = colorFromRes(context, R.color.white),
            secondary = colorFromRes(context, R.color.yellow_4),
            onSecondary = colorFromRes(context, R.color.black),
            background = colorFromRes(context, R.color.light_gray),
            surface = colorFromRes(context, R.color.light_gray),
            onBackground = colorFromRes(context, R.color.black),
            onSurface = colorFromRes(context, R.color.hint_day)
        )
    }
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
            colorScheme = appColorScheme(darkTheme),
    //typography = Typography,
    //shapes = Shapes,
    content = content
    )
}

@Composable
fun CardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val baseColorScheme = appColorScheme(darkTheme)

    val cardColorScheme = baseColorScheme.copy(
        primary = colorFromRes(LocalContext.current, if (darkTheme) R.color.dark_gray else R.color.white),
        onPrimary = colorFromRes(LocalContext.current, if (darkTheme) R.color.white else R.color.black),
    )

    MaterialTheme(
        colorScheme = cardColorScheme,
        content = content
    )
}

fun colorFromRes(context: Context, colorResId: Int): Color {
    return Color(ContextCompat.getColor(context, colorResId))
}