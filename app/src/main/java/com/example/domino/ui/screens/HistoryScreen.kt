package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domino.domain.model.GameSummary
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.RojoTrampa
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeEsmeralda
import com.example.domino.ui.theme.VerdeProfundo
import com.example.domino.ui.theme.VerdeSuperficie
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** B4: historial de partidas del usuario (users/{uid}/history). */
@Composable
fun HistorialScreen(
    partidas: List<GameSummary>,
    cargando: Boolean,
    onVolver: () -> Unit
) {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Text(text = "HISTORIAL", style = MaterialTheme.typography.headlineLarge, color = Dorado, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            when {
                cargando -> CircularProgressIndicator(color = Dorado)
                partidas.isEmpty() -> Text("Aún no has terminado ninguna partida.", color = TextoSecundario)
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(partidas) { partida ->
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
                                    text = if (partida.won) "Victoria" else "Derrota",
                                    color = if (partida.won) VerdeEsmeralda else RojoTrampa,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Sala ${partida.code} · ${formato.format(Date(partida.dateMillis))}",
                                    color = TextoSecundario
                                )
                                Text(
                                    text = "Puesto ${partida.position} de ${partida.playerCount}",
                                    color = Marfil
                                )
                            }
                            Text(text = "${partida.score} pts", color = Dorado, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BotonDomino(texto = "Volver al menú", onClick = onVolver, principal = false)
    }
}
