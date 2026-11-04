package com.example.domino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domino.domain.model.Board
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeSuperficie
import kotlinx.coroutines.delay

/** B3: cadena de fichas colocadas, con los extremos abiertos visibles arriba. */
@Composable
fun TableroView(
    tablero: Board,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()

    // Mantiene el centro de la cadena a la vista cuando crece.
    LaunchedEffect(tablero.chain.size) {
        delay(60)
        scroll.animateScrollTo(scroll.maxValue / 2)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(VerdeSuperficie, RoundedCornerShape(16.dp))
            .padding(10.dp)
            .testTag("tablero")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Izquierda: ${tablero.leftEnd() ?: "-"}",
                color = Dorado,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Derecha: ${tablero.rightEnd() ?: "-"}",
                color = Dorado,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (tablero.isEmpty()) {
                Text(
                    text = "Tablero vacío: coloca la primera ficha",
                    color = TextoSecundario
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(scroll),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(4.dp))
                    tablero.chain.forEach { ficha ->
                        FichaView(ficha = ficha, horizontal = true, tamano = 32.dp)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}
