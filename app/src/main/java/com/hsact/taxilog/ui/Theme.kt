package com.hsact.taxilog.ui

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.hsact.taxilog.R

@Composable
fun appColorScheme(isDarkTheme: Boolean): ColorScheme {
    val context = LocalContext.current
    return if (isDarkTheme) {
        darkColorScheme(
            primary = colorFromRes(context, R.color.yellow_3),
            onPrimary = colorFromRes(context, R.color.black),
            primaryContainer = colorFromRes(context, R.color.yellow_4),
            onPrimaryContainer = colorFromRes(context, R.color.black),
            secondary = colorFromRes(context, R.color.yellow_3),
            onSecondary = colorFromRes(context, R.color.black),
            secondaryContainer = colorFromRes(context, R.color.gray_800),
            onSecondaryContainer = colorFromRes(context, R.color.yellow_3),
            tertiary = colorFromRes(context, R.color.gray_700),
            onTertiary = colorFromRes(context, R.color.white),
            background = colorFromRes(context, R.color.black),
            onBackground = colorFromRes(context, R.color.white),
            surface = colorFromRes(context, R.color.dark_gray),
            onSurface = colorFromRes(context, R.color.white),
            surfaceVariant = colorFromRes(context, R.color.gray_900),
            onSurfaceVariant = colorFromRes(context, R.color.gray_300),
            outline = colorFromRes(context, R.color.gray_600)
        )
    } else {
        lightColorScheme(
            primary = colorFromRes(context, R.color.yellow_4),
            onPrimary = colorFromRes(context, R.color.black),
            primaryContainer = colorFromRes(context, R.color.yellow_1),
            onPrimaryContainer = colorFromRes(context, R.color.yellow_4),
            secondary = colorFromRes(context, R.color.yellow_4),
            onSecondary = colorFromRes(context, R.color.black),
            secondaryContainer = colorFromRes(context, R.color.yellow_1),
            onSecondaryContainer = colorFromRes(context, R.color.yellow_4),
            tertiary = colorFromRes(context, R.color.gray_600),
            onTertiary = colorFromRes(context, R.color.white),
            background = colorFromRes(context, R.color.gray_100),
            onBackground = colorFromRes(context, R.color.black),
            surface = colorFromRes(context, R.color.white),
            onSurface = colorFromRes(context, R.color.black),
            surfaceVariant = colorFromRes(context, R.color.gray_200),
            onSurfaceVariant = colorFromRes(context, R.color.gray_700),
            outline = colorFromRes(context, R.color.gray_400)
        )
    }
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
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
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val baseColorScheme = appColorScheme(darkTheme)

    val cardColorScheme = baseColorScheme.copy(
        primary = colorFromRes(
            context,
            if (darkTheme) R.color.gray_900 else R.color.white
        ),
        onPrimary = colorFromRes(
            context,
            if (darkTheme) R.color.white else R.color.black
        ),
        surface = colorFromRes(
            context,
            if (darkTheme) R.color.gray_900 else R.color.white
        ),
        onSurface = colorFromRes(
            context,
            if (darkTheme) R.color.white else R.color.black
        ),
        onBackground = colorFromRes(
            context,
            if (darkTheme) R.color.white else R.color.black
        )
    )

    MaterialTheme(
        colorScheme = cardColorScheme,
        content = content
    )
}

fun colorFromRes(context: Context, colorResId: Int): Color {
    return Color(ContextCompat.getColor(context, colorResId))
}