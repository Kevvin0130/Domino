
package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo

@Composable
fun ComoJugarScreen(
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "CÓMO JUGAR",
            style = MaterialTheme.typography.headlineLarge,
            color = Marfil
        )

        Text(
            text = "1. Recibe tus fichas",
            style = MaterialTheme.typography.titleLarge,
            color = Marfil
        )

        Text(
            text = "Cada jugador comienza con 7 fichas de dominó.",
            color = TextoSecundario
        )

        Text(
            text = "2. Coloca tus fichas",
            style = MaterialTheme.typography.titleLarge,
            color = Marfil
        )

        Text(
            text = "En tu turno, coloca una ficha que cumpla las reglas de conexión.",
            color = TextoSecundario
        )

        Text(
            text = "3. Conquista territorios",
            style = MaterialTheme.typography.titleLarge,
            color = Marfil
        )

        Text(
            text = "Aprovecha las casillas especiales para ganar puntos, evitar trampas y conseguir ventajas.",
            color = TextoSecundario
        )

        Text(
            text = "4. Gana la partida",
            style = MaterialTheme.typography.titleLarge,
            color = Marfil
        )

        Text(
            text = "Al terminar, gana quien tenga la mayor puntuación, aplicando las reglas de desempate.",
            color = TextoSecundario
        )

        Spacer(modifier = Modifier.height(8.dp))

        BotonDomino(
            texto = "Volver al menú",
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        )
    }
}