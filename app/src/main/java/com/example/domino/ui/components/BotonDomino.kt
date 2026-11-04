package com.example.domino.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.VerdeEsmeralda

@Composable
fun BotonDomino(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    principal: Boolean = true,
    altura: Dp = 48.dp,
    enabled: Boolean = true // B3: los controles de partida se deshabilitan cuando no aplican
) {
    if (principal) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.fillMaxWidth().height(altura),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeEsmeralda,
                contentColor = Color.White
            )
        ) {
            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.fillMaxWidth().height(altura),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, if (enabled) Dorado else Dorado.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Dorado
            )
        ) {
            Text(text = texto)
        }
    }
}
