package com.example.kopilka.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val TBankYellow = Color(0xFFFFDD2D)
val GreenAccent = Color(0xFF34C759)
val RedAccent = Color(0xFFFF3B30)

private val LightScheme = lightColorScheme(
    primary = TBankYellow, onPrimary = Color(0xFF0D0D0D),
    secondary = TBankYellow, onSecondary = Color(0xFF0D0D0D),
    background = Color(0xFFF5F5F7), onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF), onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF0F0F0), onSurfaceVariant = Color(0xFF8E8E93),
    error = RedAccent
)

private val DarkScheme = darkColorScheme(
    primary = TBankYellow, onPrimary = Color(0xFF0D0D0D),
    secondary = TBankYellow, onSecondary = Color(0xFF0D0D0D),
    background = Color(0xFF000000), onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1C1C1E), onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF2C2C2E), onSurfaceVariant = Color(0xFF8E8E93),
    error = RedAccent
)

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun KopilkaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkScheme else LightScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            view.context.findActivity()?.window?.let { window ->
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
