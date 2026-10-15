
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
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo

@Composable
fun CrearPartidaScreen(
    onVolver: () -> Unit,
    onCrearPartida: () -> Unit
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
            text = "CREAR",
            style = MaterialTheme.typography.headlineLarge,
            color = Dorado,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "NUEVA PARTIDA",
            style = MaterialTheme.typography.headlineMedium,
            color = Marfil
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Crea una sala e invita a tus amigos con su código.",
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        BotonDomino(
            texto = "Crear sala",
            onClick = onCrearPartida,
            modifier = Modifier.fillMaxWidth(),
            altura = 68.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        BotonDomino(
            texto = "Volver al menú",
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        )
    }
}