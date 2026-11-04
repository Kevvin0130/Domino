package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.domain.model.ActiveEvent
import com.example.domino.domain.model.EventType
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.theme.DominoTheme
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo

/** B4: aviso de evento aleatorio (mockup 8). Se muestra en un Dialog sobre el tablero. */
@Composable
fun EventoAleatorioScreen(
    evento: ActiveEvent,
    onContinuar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(24.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("evento_aleatorio")
            .background(VerdeProfundo, forma)
            .border(2.dp, Dorado, forma)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "¡EVENTO ALEATORIO!", color = Dorado, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(12.dp))
        Text(text = evento.type.icono(), fontSize = 56.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            text = evento.type.nombre(),
            color = Marfil,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(text = evento.type.descripcion(), color = TextoSecundario, textAlign = TextAlign.Center)
        if (evento.roundsLeft > 0) {
            Spacer(Modifier.height(8.dp))
            Text(text = "Dura ${evento.roundsLeft} ronda(s)", color = Dorado)
        }
        Spacer(Modifier.height(20.dp))
        BotonDomino(texto = "Continuar", onClick = onContinuar)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun EventoAleatorioPreview() {
    DominoTheme {
        EventoAleatorioScreen(
            evento = ActiveEvent(EventType.DOUBLE_SCORE, roundsLeft = 2),
            onContinuar = {}
        )
    }
}
