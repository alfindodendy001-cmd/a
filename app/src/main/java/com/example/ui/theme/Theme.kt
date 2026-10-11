package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GoldPrimary,
    onPrimary = InkBlack,
    primaryContainer = Color(0xFF452B00),
    onPrimaryContainer = AmberLight,
    secondary = IndigoSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3B1F70),
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = EmeraldTertiary,
    onTertiary = Color.White,
    background = InkBlack,
    onBackground = TextPrimaryDark,
    surface = InkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = InkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = InkBorder,
    error = Color(0xFFEF4444)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GoldPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = AmberLight,
    onPrimaryContainer = Color(0xFF452B00),
    secondary = IndigoSecondaryDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF3B1F70),
    tertiary = EmeraldDark,
    onTertiary = Color.White,
    background = ParchmentBg,
    onBackground = TextPrimaryLight,
    surface = ParchmentSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = ParchmentSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFD6CEBD),
    error = Color(0xFFDC2626)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep custom novelist theme consistent
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
