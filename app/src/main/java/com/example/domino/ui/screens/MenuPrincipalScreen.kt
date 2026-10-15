
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo

@Composable
fun MenuPrincipalScreen(
    onJugar: () -> Unit,
    onCrearSala: () -> Unit,
    onUnirseSala: () -> Unit,
    onComoJugar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DOMINO",
            style = MaterialTheme.typography.headlineLarge,
            color = Dorado,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "CONQUEST",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "La estrategia conquista el tablero",
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "MENÚ PRINCIPAL",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonDomino(
                texto = "Jugar",
                onClick = onJugar,
                altura = 108.dp
            )

            BotonDomino(
                texto = "Crear sala",
                onClick = onCrearSala,
                altura = 108.dp,
                principal = false
            )

            BotonDomino(
                texto = "Unirse a una sala",
                onClick = onUnirseSala,
                altura = 108.dp,
                principal = false
            )

            BotonDomino(
                texto = "Cómo jugar",
                onClick = onComoJugar,
                altura = 108.dp,
                principal = false
            )
        }
    }
}