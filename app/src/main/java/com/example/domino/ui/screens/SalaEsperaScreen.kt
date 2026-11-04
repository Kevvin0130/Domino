package com.example.domino.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.domain.GameEngineImpl
import com.example.domino.domain.model.GameState
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.components.JugadorCard
import com.example.domino.ui.components.PantallaCargando
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo

/**
 * B4: sala de espera. Muestra el código, los jugadores que ya entraron y,
 * solo para el anfitrión, el botón de iniciar (mínimo 2 jugadores).
 */
@Composable
fun SalaEsperaScreen(
    codigo: String,
    estado: GameState?,
    miId: String,
    onIniciar: () -> Unit,
    onSalir: () -> Unit
) {
    if (estado == null) {
        PantallaCargando(texto = "Conectando con la sala $codigo…", onSalir = onSalir)
        return
    }

    val contexto = LocalContext.current
    val esAnfitrion = estado.hostId == miId
    val minimo = GameEngineImpl.MIN_PLAYERS
    val maximo = GameEngineImpl.MAX_PLAYERS
    val puedeIniciar = esAnfitrion && estado.players.size >= minimo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SALA DE ESPERA",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            color = Marfil
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Código de la sala", color = TextoSecundario)
        Text(
            text = codigo,
            color = Dorado,
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 6.sp,
            modifier = Modifier.testTag("codigo_sala")
        )
        Spacer(modifier = Modifier.height(8.dp))
        BotonDomino(
            texto = "Compartir código",
            principal = false,
            altura = 40.dp,
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Únete a mi partida de Domino Conquest con el código $codigo")
                }
                contexto.startActivity(Intent.createChooser(intent, "Compartir código"))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Jugadores ${estado.players.size}/$maximo",
            color = Marfil,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            estado.players.forEach { jugador ->
                JugadorCard(
                    jugador = jugador,
                    esTurno = false,
                    esYo = jugador.id == miId,
                    esAnfitrion = jugador.id == estado.hostId,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = when {
                esAnfitrion && puedeIniciar -> "Todo listo. Inicia cuando quieras."
                esAnfitrion -> "Necesitas al menos $minimo jugadores para iniciar."
                else -> "Esperando a que el anfitrión inicie la partida…"
            },
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (esAnfitrion) {
            BotonDomino(
                texto = "Iniciar partida",
                onClick = onIniciar,
                enabled = puedeIniciar,
                altura = 56.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        BotonDomino(texto = "Salir de la sala", onClick = onSalir, principal = false)
    }
}
