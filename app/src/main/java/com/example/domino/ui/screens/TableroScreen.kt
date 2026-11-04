package com.example.domino.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.domain.model.EventType
import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.PowerType
import com.example.domino.domain.model.Side
import com.example.domino.domain.model.Tile
import com.example.domino.ui.components.ControlesPartida
import com.example.domino.ui.components.FichaView
import com.example.domino.ui.components.JugadorCard
import com.example.domino.ui.components.PantallaCargando
import com.example.domino.ui.components.TableroView
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo
import com.example.domino.ui.viewmodel.GameUiState

/**
 * B4: tablero de juego. Solo dibuja [ui] y reenvía las acciones al GameViewModel.
 * Turno y puntaje de todos los jugadores quedan siempre visibles arriba.
 * También hospeda las dos pantallas superpuestas: Poderes y Evento aleatorio.
 */
@Composable
fun TableroScreen(
    ui: GameUiState,
    onSeleccionarFicha: (Tile) -> Unit,
    onSeleccionarPoder: (PowerType?) -> Unit,
    onSeleccionarObjetivo: (String, Tile) -> Unit,
    onConfirmarIntercambio: () -> Unit,
    onColocar: (Side) -> Unit,
    onRobar: () -> Unit,
    onAbandonar: () -> Unit,
    onSalir: () -> Unit
) {
    val estado = ui.state
    if (estado == null) {
        PantallaCargando(texto = "Cargando la partida…", onSalir = onSalir)
        return
    }

    var mostrarPoderes by remember { mutableStateOf(false) }
    var confirmarAbandono by remember { mutableStateOf(false) }

    // --- Evento aleatorio: se muestra una vez por cada evento nuevo ---
    val eventoActual = eventoReciente(estado)
    val claveEvento = "${estado.round}-${estado.lastMessage}"
    var ultimaClave by remember { mutableStateOf("") }
    var eventoPendiente by remember { mutableStateOf<EventType?>(null) }
    LaunchedEffect(claveEvento) {
        if (eventoActual != null && claveEvento != ultimaClave) {
            ultimaClave = claveEvento
            eventoPendiente = eventoActual
        }
    }

    // El botón "atrás" no saca al jugador de la partida por accidente.
    BackHandler {
        when {
            eventoPendiente != null -> eventoPendiente = null
            mostrarPoderes -> mostrarPoderes = false
            else -> confirmarAbandono = true
        }
    }

    if (eventoPendiente != null) {
        EventoScreen(evento = eventoPendiente!!, onContinuar = { eventoPendiente = null })
        return
    }
    if (mostrarPoderes) {
        PoderesScreen(
            ui = ui,
            onSeleccionarFicha = onSeleccionarFicha,
            onSeleccionarPoder = onSeleccionarPoder,
            onSeleccionarObjetivo = onSeleccionarObjetivo,
            onConfirmarIntercambio = onConfirmarIntercambio,
            onCerrar = { mostrarPoderes = false }
        )
        return
    }

    val yo = estado.players.firstOrNull { it.id == ui.myId }
    val miMano = yo?.hand.orEmpty()
    val jugadorEnTurno = estado.currentPlayer()
    val fichaElegida = ui.selectedTile

    // Qué permite el motor ahora mismo (el ViewModel ya lo calculó en ui.validMoves).
    fun puedeColocar(lado: Side): Boolean =
        fichaElegida != null && ui.validMoves.any {
            it.tile == fichaElegida && it.side == lado && it.power == ui.selectedPower
        }
    val hayJugadaNormal = ui.validMoves.any { it.power == null }
    val puedeRobar = ui.isMyTurn && !hayJugadaNormal
    val textoRobar = if (estado.drawPile.isNotEmpty()) "Robar (${estado.drawPile.size})" else "Pasar turno"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Jugadores: turno y puntaje siempre visibles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            estado.players.forEach { jugador ->
                JugadorCard(
                    jugador = jugador,
                    esTurno = jugador.id == jugadorEnTurno?.id && !jugador.abandoned,
                    esYo = jugador.id == ui.myId,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 2. Información de la ronda
        InfoPartida(estado)

        // 3. Tablero
        TableroView(tablero = estado.board, modifier = Modifier.weight(1f))

        // 4. Turno
        Text(
            text = if (ui.isMyTurn) "Tu turno" else "Turno de ${jugadorEnTurno?.name ?: "-"}",
            color = if (ui.isMyTurn) Dorado else TextoSecundario,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("texto_turno")
        )

        // 5. Mi mano
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            miMano.forEach { ficha ->
                FichaView(
                    ficha = ficha,
                    tamano = 40.dp,
                    seleccionada = ficha == fichaElegida,
                    jugable = ui.isMyTurn && ui.validMoves.any { it.tile == ficha && it.power == null },
                    onClick = { onSeleccionarFicha(ficha) }
                )
            }
        }

        // 6. Controles
        ControlesPartida(
            tableroVacio = estado.board.isEmpty(),
            puedeColocarIzquierda = puedeColocar(Side.LEFT),
            puedeColocarDerecha = puedeColocar(Side.RIGHT),
            puedeRobar = puedeRobar,
            textoRobar = textoRobar,
            puedePoderes = ui.isMyTurn,
            poderActivo = ui.selectedPower,
            onColocarIzquierda = { onColocar(Side.LEFT) },
            onColocarDerecha = { onColocar(Side.RIGHT) },
            onRobar = onRobar,
            onPoderes = { mostrarPoderes = true },
            onAbandonar = { confirmarAbandono = true }
        )
    }

    if (confirmarAbandono) {
        AlertDialog(
            onDismissRequest = { confirmarAbandono = false },
            title = { Text("¿Abandonar la partida?") },
            text = { Text("Saldrás de la partida y no podrás ganarla.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarAbandono = false
                    onAbandonar()
                }) { Text("Abandonar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarAbandono = false }) { Text("Seguir jugando") }
            }
        )
    }
}

@Composable
private fun InfoPartida(estado: GameState) {
    val evento = estado.activeEvent
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Ronda ${estado.round}/${estado.maxRounds}", color = Marfil, fontSize = 12.sp)
            Text("Pozo: ${estado.drawPile.size}", color = Marfil, fontSize = 12.sp)
            Text(if (estado.direction == 1) "Sentido →" else "Sentido ←", color = Marfil, fontSize = 12.sp)
        }
        if (evento != null && evento.roundsLeft > 0) {
            Text(
                text = "Evento activo: ${evento.type.nombre()} (${evento.roundsLeft} rondas)",
                color = Dorado,
                fontSize = 12.sp
            )
        }
        if (estado.lastMessage.isNotBlank()) {
            Text(text = estado.lastMessage, color = TextoSecundario, fontSize = 12.sp)
        }
    }
}

/**
 * El motor no guarda "evento nuevo", solo deja `activeEvent` y un `lastMessage`.
 * Un evento recién disparado es aquel cuyo texto sigue siendo el último mensaje
 * (el siguiente movimiento lo reemplaza). Si A cambia los textos del motor, ajustar aquí.
 */
private fun eventoReciente(estado: GameState): EventType? {
    val evento = estado.activeEvent ?: return null
    val prefijo = when (evento.type) {
        EventType.TILE_STORM -> "Tile storm"
        EventType.DOUBLE_SCORE -> "Double score"
        EventType.FORCED_DRAW -> "Forced draw"
        EventType.TURN_REVERSAL -> "Turn order reversed"
    }
    return if (estado.lastMessage.startsWith(prefijo)) evento.type else null
}
