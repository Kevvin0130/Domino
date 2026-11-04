package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.domino.domain.model.Player
import com.example.domino.domain.model.PowerType
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.components.PoderButton
import com.example.domino.ui.theme.DominoTheme
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo

/**
 * B4: panel para elegir el poder ANTES de mover. Se muestra dentro de un Dialog
 * desde TableroScreen. Tocar el poder ya seleccionado lo quita (vuelve a null).
 */
@Composable
fun SeleccionPoderesScreen(
    jugador: Player,
    seleccionado: PowerType?,
    puedeUsar: Boolean,
    onSeleccionar: (PowerType?) -> Unit,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(VerdeProfundo, RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PODERES",
            style = MaterialTheme.typography.headlineSmall,
            color = Dorado,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Elige un poder antes de jugar tu ficha. Cada uno tiene usos limitados.",
            color = TextoSecundario,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(16.dp))

        // Cuadrícula 2x2 hecha con filas, para no anidar listas con scroll dentro del Dialog.
        PowerType.entries.chunked(2).forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                fila.forEach { poder ->
                    PoderButton(
                        poder = poder,
                        usosRestantes = jugador.usesLeft(poder),
                        seleccionado = seleccionado == poder,
                        habilitado = puedeUsar,
                        onClick = { onSeleccionar(if (seleccionado == poder) null else poder) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (!puedeUsar) {
            Text(text = "Solo puedes usar poderes en tu turno.", color = TextoSecundario)
            Spacer(Modifier.height(8.dp))
        }
        BotonDomino(texto = "Cerrar", onClick = onCerrar, principal = false)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SeleccionPoderesPreview() {
    DominoTheme {
        SeleccionPoderesScreen(
            jugador = Player(id = "u1", name = "Jorge"),
            seleccionado = PowerType.SKIP,
            puedeUsar = true,
            onSeleccionar = {},
            onCerrar = {}
        )
    }
}
