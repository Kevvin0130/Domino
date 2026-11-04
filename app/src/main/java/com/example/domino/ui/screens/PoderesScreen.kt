package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domino.domain.model.PowerType
import com.example.domino.domain.model.Tile
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.components.FichaView
import com.example.domino.ui.components.PoderButton
import com.example.domino.ui.components.nombre
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo
import com.example.domino.ui.viewmodel.GameUiState

/**
 * B4: selección de poderes (se muestra encima del tablero).
 *  - Comodín / Salto / Inversión: se elige el poder y, al cerrar, se coloca la ficha con los botones del tablero.
 *  - Intercambio: se elige tu ficha, el rival y su ficha (oculta) y se confirma aquí.
 */
@Composable
fun PoderesScreen(
    ui: GameUiState,
    onSeleccionarFicha: (Tile) -> Unit,
    onSeleccionarPoder: (PowerType?) -> Unit,
    onSeleccionarObjetivo: (String, Tile) -> Unit,
    onConfirmarIntercambio: () -> Unit,
    onCerrar: () -> Unit
) {
    val estado = ui.state ?: return
    val yo = estado.players.firstOrNull { it.id == ui.myId } ?: return
    val rivales = estado.players.filter { it.id != ui.myId && !it.abandoned }
    val habilitado = ui.isMyTurn && ui.selectedTile != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "PODERES", style = MaterialTheme.typography.headlineMedium, color = Dorado)
        Text(
            text = if (ui.selectedTile == null) {
                "Primero toca una ficha de tu mano y luego elige el poder."
            } else {
                "Ficha elegida: ${ui.selectedTile.left}-${ui.selectedTile.right}"
            },
            color = TextoSecundario
        )

        PowerType.entries.forEach { poder ->
            PoderButton(
                poder = poder,
                usosRestantes = yo.usesLeft(poder),
                seleccionado = ui.selectedPower == poder,
                habilitado = habilitado,
                onClick = { onSeleccionarPoder(if (ui.selectedPower == poder) null else poder) }
            )
        }

        if (ui.selectedPower == PowerType.SWAP) {
            Text(text = "Tu ficha a entregar", color = Marfil)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                yo.hand.forEach { ficha ->
                    FichaView(
                        ficha = ficha,
                        tamano = 36.dp,
                        seleccionada = ficha == ui.selectedTile,
                        onClick = { onSeleccionarFicha(ficha) }
                    )
                }
            }
            Text(text = "Ficha del rival (están ocultas)", color = Marfil)
            rivales.forEach { rival ->
                Text(text = rival.name, color = TextoSecundario)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rival.hand.forEach { ficha ->
                        FichaView(
                            ficha = ficha,
                            tamano = 36.dp,
                            oculta = true,
                            seleccionada = ui.swapTargetId == rival.id && ui.swapTargetTile == ficha,
                            onClick = { onSeleccionarObjetivo(rival.id, ficha) }
                        )
                    }
                }
            }
            BotonDomino(
                texto = "Confirmar intercambio",
                onClick = {
                    onConfirmarIntercambio()
                    onCerrar()
                },
                enabled = ui.selectedTile != null && ui.swapTargetId != null && ui.swapTargetTile != null
            )
        } else if (ui.selectedPower != null) {
            Text(
                text = "Poder activo: ${ui.selectedPower.nombre()}. Cierra esta pantalla y coloca la ficha.",
                color = Marfil
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        BotonDomino(texto = "Listo", onClick = onCerrar, principal = false)
    }
}
