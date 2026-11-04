package com.example.domino.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domino.domain.model.PowerType

/**
 * B3: botones de la partida. No decide reglas: recibe qué está permitido y avisa con callbacks.
 */
@Composable
fun ControlesPartida(
    tableroVacio: Boolean,
    puedeColocarIzquierda: Boolean,
    puedeColocarDerecha: Boolean,
    puedeRobar: Boolean,
    textoRobar: String,
    puedePoderes: Boolean,
    poderActivo: PowerType?,
    onColocarIzquierda: () -> Unit,
    onColocarDerecha: () -> Unit,
    onRobar: () -> Unit,
    onPoderes: () -> Unit,
    onAbandonar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (tableroVacio) {
                BotonDomino(
                    texto = "Colocar ficha",
                    onClick = onColocarDerecha,
                    enabled = puedeColocarDerecha,
                    altura = 44.dp,
                    modifier = Modifier.weight(1f).testTag("btn_derecha")
                )
            } else {
                BotonDomino(
                    texto = "◀ Izquierda",
                    onClick = onColocarIzquierda,
                    enabled = puedeColocarIzquierda,
                    altura = 44.dp,
                    modifier = Modifier.weight(1f).testTag("btn_izquierda")
                )
                BotonDomino(
                    texto = "Derecha ▶",
                    onClick = onColocarDerecha,
                    enabled = puedeColocarDerecha,
                    altura = 44.dp,
                    modifier = Modifier.weight(1f).testTag("btn_derecha")
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonDomino(
                texto = textoRobar,
                onClick = onRobar,
                enabled = puedeRobar,
                principal = false,
                altura = 44.dp,
                modifier = Modifier.weight(1f).testTag("btn_robar")
            )
            BotonDomino(
                texto = if (poderActivo == null) "Poderes" else "Poder: ${poderActivo.nombre()}",
                onClick = onPoderes,
                enabled = puedePoderes,
                principal = false,
                altura = 44.dp,
                modifier = Modifier.weight(1f).testTag("btn_poderes")
            )
        }
        BotonDomino(
            texto = "Abandonar partida",
            onClick = onAbandonar,
            principal = false,
            altura = 40.dp,
            modifier = Modifier.testTag("btn_abandonar")
        )
    }
}
