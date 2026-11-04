package com.example.domino.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.domain.model.Tile
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeEsmeralda
import com.example.domino.ui.theme.VerdeProfundo
import com.example.domino.ui.theme.VerdeSuperficie

/**
 * B3: una ficha de dominó dibujada con puntos.
 *
 * @param tamano lado corto de la ficha; el lado largo mide el doble.
 * @param horizontal true para el tablero, false (vertical) para la mano.
 * @param seleccionada borde dorado grueso.
 * @param jugable borde verde: se puede usar en este turno.
 * @param oculta muestra el reverso (fichas del oponente en el Intercambio).
 */
@Composable
fun FichaView(
    ficha: Tile,
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    tamano: Dp = 40.dp,
    seleccionada: Boolean = false,
    jugable: Boolean = false,
    oculta: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val forma = RoundedCornerShape(8.dp)
    val colorBorde = when {
        seleccionada -> Dorado
        jugable -> VerdeEsmeralda
        else -> TextoSecundario
    }
    val grosorBorde = when {
        seleccionada -> 3.dp
        jugable -> 2.dp
        else -> 1.dp
    }
    val dimension = if (horizontal) {
        Modifier.size(width = tamano * 2, height = tamano)
    } else {
        Modifier.size(width = tamano, height = tamano * 2)
    }
    val etiqueta = if (oculta) "ficha_oculta" else "ficha_${ficha.left}_${ficha.right}"

    var base = modifier
        .then(dimension)
        .testTag(etiqueta)
        .background(if (oculta) VerdeEsmeralda else Marfil, forma)
        .border(grosorBorde, colorBorde, forma)
    if (onClick != null) base = base.clickable(onClick = onClick)

    if (oculta) {
        Box(modifier = base, contentAlignment = Alignment.Center) {
            Text(text = "?", color = Marfil, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    } else if (horizontal) {
        Row(modifier = base, verticalAlignment = Alignment.CenterVertically) {
            Pips(ficha.left, Modifier.weight(1f).fillMaxHeight())
            Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.8f).background(VerdeSuperficie))
            Pips(ficha.right, Modifier.weight(1f).fillMaxHeight())
        }
    } else {
        Column(modifier = base, horizontalAlignment = Alignment.CenterHorizontally) {
            Pips(ficha.left, Modifier.weight(1f).fillMaxWidth())
            Box(modifier = Modifier.height(2.dp).fillMaxWidth(0.8f).background(VerdeSuperficie))
            Pips(ficha.right, Modifier.weight(1f).fillMaxWidth())
        }
    }
}

@Composable
private fun Pips(valor: Int, modifier: Modifier) {
    Canvas(modifier = modifier) {
        val radio = size.minDimension * 0.09f
        posicionesPips(valor).forEach { (fx, fy) ->
            drawCircle(
                color = VerdeProfundo,
                radius = radio,
                center = Offset(size.width * fx, size.height * fy)
            )
        }
    }
}

/** Posiciones (fracción x, fracción y) de los puntos para un valor de 0 a 6. */
private fun posicionesPips(valor: Int): List<Pair<Float, Float>> {
    val a = 0.28f
    val c = 0.5f
    val b = 0.72f
    return when (valor) {
        0 -> emptyList()
        1 -> listOf(c to c)
        2 -> listOf(a to a, b to b)
        3 -> listOf(a to a, c to c, b to b)
        4 -> listOf(a to a, b to a, a to b, b to b)
        5 -> listOf(a to a, b to a, c to c, a to b, b to b)
        else -> listOf(a to a, b to a, a to c, b to c, a to b, b to b)
    }
}
