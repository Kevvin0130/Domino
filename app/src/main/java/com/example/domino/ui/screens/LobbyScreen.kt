package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.domain.model.GameState
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo
import com.example.domino.ui.theme.VerdeSuperficie

@Composable
fun SalaEsperaScreen(
    codigo: String,
    estado: GameState,
    miId: String,
    onIniciar: () -> Unit,
    onSalir: () -> Unit,
    modifier: Modifier = Modifier
) {
    val esAnfitrion = estado.players.firstOrNull()?.id == miId
    val puedeIniciar = esAnfitrion && estado.players.size >= 2

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "SALA DE ESPERA",
                style = MaterialTheme.typography.titleMedium,
                color = TextoSecundario
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = codigo,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Dorado,
                modifier = Modifier.testTag("codigo_sala")
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Jugadores (${estado.players.size}/4):",
                style = MaterialTheme.typography.titleSmall,
                color = Marfil
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(estado.players) { jugador ->
                    val esYo = jugador.id == miId
                    val esHost = estado.players.firstOrNull()?.id == jugador.id
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = VerdeSuperficie),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (esYo) "${jugador.name} (Tú)" else jugador.name,
                                color = Marfil,
                                fontWeight = FontWeight.Bold
                            )
                            if (esHost) {
                                Text(
                                    text = "ANFITRIÓN",
                                    color = Dorado,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            if (!esAnfitrion) {
                Text(
                    text = "Esperando a que el anfitrión inicie la partida…",
                    color = TextoSecundario,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else if (estado.players.size < 2) {
                Text(
                    text = "Se necesitan al menos 2 jugadores para iniciar.",
                    color = TextoSecundario,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            if (esAnfitrion) {
                BotonDomino(
                    texto = "Iniciar partida",
                    enabled = puedeIniciar,
                    onClick = onIniciar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("boton_iniciar_partida")
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            BotonDomino(
                texto = "Salir de la sala",
                principal = false,
                onClick = onSalir,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
