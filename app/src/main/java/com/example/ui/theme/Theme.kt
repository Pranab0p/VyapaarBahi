package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryBlue,
    secondary = SuccessGreen,
    tertiary = ErrantRed
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryWhite,
    primaryContainer = PrimaryBlueContainer,
    secondary = SuccessGreen,
    onSecondary = OnPrimaryWhite,
    secondaryContainer = SuccessGreenContainer,
    error = ErrantRed,
    onError = OnPrimaryWhite,
    errorContainer = ErrantRedContainer,
    background = LedgerBackground,
    onBackground = TextOnSurface,
    surface = LedgerSurfaceLowest,
    onSurface = TextOnSurface,
    surfaceVariant = LedgerSurfaceLow,
    onSurfaceVariant = TextOnSurfaceVariant,
    outline = OutlineVariant
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color by default to keep the crisp, professional colors consistent
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
