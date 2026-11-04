package com.example.domino.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.Player
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.components.PantallaCargando
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo
import com.example.domino.ui.theme.VerdeSuperficie

/** B4: resultado final con ranking. Desempate: menos poderes usados (regla del motor). */
@Composable
fun ResultadoScreen(
    estado: GameState?,
    miId: String,
    onJugarDeNuevo: () -> Unit,
    onSalir: () -> Unit
) {
    if (estado == null) {
        PantallaCargando(texto = "Cargando resultado…", onSalir = onSalir)
        return
    }

    val ranking = estado.players
        .filter { !it.abandoned }
        .sortedWith(compareByDescending<Player> { it.score }.thenBy { it.powersUsed })
    val ganador = estado.players.firstOrNull { it.id == estado.winnerId }
    val hayEmpatePuntos = ranking.size > 1 && ranking[0].score == ranking[1].score
    val titulo = when {
        ganador == null -> "¡EMPATE!"
        ganador.id == miId -> "¡GANASTE!"
        else -> "GANÓ ${ganador.name.uppercase()}"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineLarge,
            color = Dorado,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("titulo_resultado")
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (estado.lastMessage.isNotBlank()) {
            Text(text = estado.lastMessage, color = TextoSecundario, textAlign = TextAlign.Center)
        }
        if (hayEmpatePuntos) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Empate en puntos: se desempata por menos poderes usados.",
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ranking.forEachIndexed { indice, jugador ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VerdeSuperficie, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${indice + 1}. ${jugador.name}" + if (jugador.id == miId) " (tú)" else "",
                            color = Marfil,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Poderes usados: ${jugador.powersUsed} · Fichas: ${jugador.hand.size}",
                            color = TextoSecundario
                        )
                    }
                    Text(
                        text = "${jugador.score} pts",
                        color = Dorado,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        if (estado.hostId == miId) {
            BotonDomino(texto = "Jugar de nuevo", onClick = onJugarDeNuevo, altura = 56.dp)
        } else {
            Text(text = "El anfitrión puede iniciar otra partida.", color = TextoSecundario)
        }
        Spacer(modifier = Modifier.height(12.dp))
        BotonDomino(texto = "Volver al menú", onClick = onSalir, principal = false)
    }
}
