
package com.example.domino.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DominoColorScheme = darkColorScheme(
    primary = VerdeEsmeralda,
    onPrimary = BlancoSuave,

    secondary = Dorado,
    onSecondary = VerdeProfundo,

    tertiary = AzulPortal,
    onTertiary = BlancoSuave,

    background = VerdeProfundo,
    onBackground = Marfil,

    surface = VerdeSuperficie,
    onSurface = Marfil,

    error = RojoTrampa,
    onError = BlancoSuave
)

@Composable
fun DominoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DominoColorScheme,
        typography = Typography,
        content = content
    )
}