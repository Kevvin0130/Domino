package com.example.domino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.domain.model.PowerType
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeEsmeralda
import com.example.domino.ui.theme.VerdeSuperficie

/** Textos en español de los poderes del motor (el dominio usa nombres en inglés). */
fun PowerType.nombre(): String = when (this) {
    PowerType.WILDCARD -> "Comodín"
    PowerType.SKIP -> "Salto"
    PowerType.SWAP -> "Intercambio"
    PowerType.REVERSE -> "Inversión"
}

fun PowerType.descripcion(): String = when (this) {
    PowerType.WILDCARD -> "Coloca una ficha aunque no coincida con el extremo."
    PowerType.SKIP -> "El siguiente jugador pierde su turno."
    PowerType.SWAP -> "Cambia una de tus fichas por una de un rival."
    PowerType.REVERSE -> "Invierte el sentido de los turnos."
}

fun PowerType.icono(): String = when (this) {
    PowerType.WILDCARD -> "🃏"
    PowerType.SKIP -> "⏭"
    PowerType.SWAP -> "🔄"
    PowerType.REVERSE -> "↩"
}

/** B3: botón de un poder con sus usos restantes. Se deshabilita solo si no quedan usos. */
@Composable
fun PoderButton(
    poder: PowerType,
    usosRestantes: Int,
    modifier: Modifier = Modifier,
    seleccionado: Boolean = false,
    habilitado: Boolean = true,
    onClick: () -> Unit
) {
    val activo = habilitado && usosRestantes > 0
    val forma = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("poder_${poder.name}")
            .alpha(if (activo) 1f else 0.45f)
            .background(if (seleccionado) VerdeEsmeralda else VerdeSuperficie, forma)
            .border(if (seleccionado) 2.dp else 1.dp, Dorado, forma)
            .clickable(enabled = activo, onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = poder.icono(), fontSize = 24.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = poder.nombre(), color = Marfil, fontWeight = FontWeight.Bold)
            Text(text = poder.descripcion(), color = TextoSecundario, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = "x$usosRestantes", color = Dorado, fontWeight = FontWeight.ExtraBold)
    }
}
