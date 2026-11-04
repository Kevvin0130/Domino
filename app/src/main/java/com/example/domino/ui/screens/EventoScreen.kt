package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.domain.model.EventType
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.theme.AzulPortal
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.RojoTrampa

fun EventType.nombre(): String = when (this) {
    EventType.TILE_STORM -> "Tormenta de fichas"
    EventType.DOUBLE_SCORE -> "Puntaje doble"
    EventType.FORCED_DRAW -> "Robo forzado"
    EventType.TURN_REVERSAL -> "Giro de turnos"
}

fun EventType.descripcion(): String = when (this) {
    EventType.TILE_STORM -> "Todas las fichas volvieron al pozo y se repartieron de nuevo."
    EventType.DOUBLE_SCORE -> "Las fichas que se coloquen valen el doble durante 2 rondas."
    EventType.FORCED_DRAW -> "El jugador en turno debe robar una ficha del pozo."
    EventType.TURN_REVERSAL -> "El sentido de los turnos se invirtió."
}

fun EventType.icono(): String = when (this) {
    EventType.TILE_STORM -> "🌪"
    EventType.DOUBLE_SCORE -> "✨"
    EventType.FORCED_DRAW -> "🎴"
    EventType.TURN_REVERSAL -> "🔁"
}

/** B4: aviso a pantalla completa cuando ocurre un evento aleatorio. */
@Composable
fun EventoScreen(
    evento: EventType,
    onContinuar: () -> Unit
) {
    val color = when (evento) {
        EventType.TILE_STORM -> AzulPortal
        EventType.DOUBLE_SCORE -> Dorado
        EventType.FORCED_DRAW -> RojoTrampa
        EventType.TURN_REVERSAL -> Marfil
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.domino.ui.theme.VerdeProfundo)
            .padding(32.dp)
            .testTag("pantalla_evento"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "¡EVENTO ALEATORIO!", color = Marfil, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = evento.icono(), fontSize = 72.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = evento.nombre(), color = color, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = evento.descripcion(), color = Marfil, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        BotonDomino(texto = "Continuar", onClick = onContinuar)
    }
}
