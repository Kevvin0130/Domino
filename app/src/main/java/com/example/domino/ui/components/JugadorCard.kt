package com.example.domino.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.domain.model.Player
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeSuperficie

/**
 * B3: tarjeta de jugador. El turno y el puntaje SIEMPRE son visibles (requisito del taller).
 */
@Composable
fun JugadorCard(
    jugador: Player,
    esTurno: Boolean,
    modifier: Modifier = Modifier,
    esYo: Boolean = false,
    esAnfitrion: Boolean = false
) {
    val borde = if (esTurno) BorderStroke(2.dp, Dorado) else BorderStroke(1.dp, Color.Transparent)
    Card(
        modifier = modifier.testTag("jugador_${jugador.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (jugador.abandoned) VerdeSuperficie.copy(alpha = 0.4f) else VerdeSuperficie
        ),
        border = borde
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = if (esTurno) "● TURNO" else if (jugador.abandoned) "Abandonó" else " ",
                color = Dorado,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = buildString {
                    append(jugador.name)
                    if (esYo) append(" (tú)")
                },
                color = Marfil,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${jugador.score} pts",
                color = Dorado,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
            Text(
                text = "${jugador.hand.size} fichas",
                color = TextoSecundario,
                fontSize = 11.sp
            )
            Text(
                text = if (esAnfitrion) "Anfitrión" else "Poderes: ${jugador.powersUsed}",
                color = TextoSecundario,
                fontSize = 11.sp
            )
        }
    }
}
