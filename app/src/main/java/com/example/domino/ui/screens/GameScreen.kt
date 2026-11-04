package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.domino.domain.model.GamePhase
import com.example.domino.domain.model.PowerType
import com.example.domino.domain.model.Side
import com.example.domino.domain.model.Tile
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.VerdeProfundo
import com.example.domino.ui.viewmodel.GameUiState

@Composable
fun GameScreen(
    ui: GameUiState,
    onIniciar: () -> Unit,
    onSeleccionarFicha: (Tile) -> Unit,
    onSeleccionarPoder: (PowerType?) -> Unit,
    onSeleccionarObjetivo: (String, Tile) -> Unit,
    onConfirmarIntercambio: () -> Unit,
    onColocar: (Side) -> Unit,
    onRobar: () -> Unit,
    onAbandonar: () -> Unit,
    onReiniciar: () -> Unit,
    onSalir: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = ui.state

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VerdeProfundo)
    ) {
        when {
            ui.loading || state == null -> {
                CircularProgressIndicator(
                    color = Marfil,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            ui.error != null -> {
                Text(
                    text = ui.error,
                    color = Marfil,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.phase == GamePhase.WAITING -> {
                SalaEsperaScreen(
                    codigo = state.code,
                    estado = state,
                    miId = ui.myId,
                    onIniciar = onIniciar,
                    onSalir = onSalir
                )
            }
            state.phase == GamePhase.FINISHED -> {
                ResultadoScreen(
                    estado = state,
                    miId = ui.myId,
                    onJugarDeNuevo = onReiniciar,
                    onSalir = onSalir
                )
            }
            else -> {
                TableroScreen(
                    ui = ui,
                    onSeleccionarFicha = onSeleccionarFicha,
                    onSeleccionarPoder = onSeleccionarPoder,
                    onSeleccionarObjetivo = onSeleccionarObjetivo,
                    onConfirmarIntercambio = onConfirmarIntercambio,
                    onColocar = onColocar,
                    onRobar = onRobar,
                    onAbandonar = onAbandonar,
                    onSalir = onSalir
                )
            }
        }
    }
}