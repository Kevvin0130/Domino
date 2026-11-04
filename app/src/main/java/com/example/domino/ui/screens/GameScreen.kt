package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domino.domain.model.GamePhase
import com.example.domino.domain.model.PowerType
import com.example.domino.domain.model.Side
import com.example.domino.domain.model.Tile
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.VerdeProfundo
import com.example.domino.ui.viewmodel.GameUiState

/**
 * B5: una sola ruta ("partida/{codigo}") muestra la pantalla correcta según la FASE
 * que llega de Firestore. Así, cuando el anfitrión inicia o la partida termina,
 * todos los dispositivos cambian de pantalla solos, sin navegar a mano.
 *
 *   WAITING     -> SalaEsperaScreen
 *   IN_PROGRESS -> TableroScreen
 *   FINISHED    -> ResultadoScreen
 */
@Composable
fun PartidaScreen(
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
    onSalir: () -> Unit
) {
    val estado = ui.state

    when {
        estado == null && ui.loading -> Centrado {
            CircularProgressIndicator(color = Dorado)
            Spacer(Modifier.height(12.dp))
            Text("Conectando con la sala…", color = Marfil)
        }

        estado == null -> Centrado {
            Text("La sala no existe o fue cerrada.", color = Marfil)
            Spacer(Modifier.height(16.dp))
            BotonDomino(texto = "Volver al menú", onClick = onSalir)
        }

        estado.phase == GamePhase.WAITING -> SalaEsperaScreen(
            codigo = estado.code,
            estado = estado,
            miId = ui.myId,
            onIniciar = onIniciar,
            onSalir = onAbandonar
        )

        estado.phase == GamePhase.IN_PROGRESS -> TableroScreen(
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

        else -> ResultadoScreen(
            estado = estado,
            miId = ui.myId,
            onJugarDeNuevo = onReiniciar,
            onSalir = onSalir
        )
    }
}

@Composable
private fun Centrado(contenido: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        contenido()
    }
}
