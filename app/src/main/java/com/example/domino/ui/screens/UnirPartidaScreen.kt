
package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo
import com.example.domino.ui.theme.VerdeSuperficie

@Composable
fun UnirPartidaScreen(
    onVolver: () -> Unit,
    onUnirse: (String) -> Unit
) {
    var codigo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "UNIRSE",
            style = MaterialTheme.typography.headlineLarge,
            color = Dorado,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "A PARTIDA",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingresa el código de la sala",
            color = TextoSecundario
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoCodigo(
            codigo = codigo,
            onCodigoChange = { codigo = it } //Funcion Lambda
        )

        Spacer(modifier = Modifier.height(28.dp))

        BotonDomino(
            texto = "Unirse a la partida",
            onClick = {
                if (codigo.length == 6) {
                    onUnirse(codigo)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            altura = 60.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        BotonDomino(
            texto = "Volver al menú",
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CampoCodigo(
    codigo: String,
    onCodigoChange: (String) -> Unit
) {
    BasicTextField(
        value = codigo,
        onValueChange = { nuevoTexto ->
            onCodigoChange(
                nuevoTexto
                    .filter { it.isLetterOrDigit() }
                    .uppercase()
                    .take(6)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Ascii //Esto configura el tipo de teclado que Android solicita para introducir el código.
        ),
        cursorBrush = SolidColor(Dorado),
        textStyle = MaterialTheme.typography.titleLarge.copy(
            color = Color.Transparent
        ),
        decorationBox = { campo ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                campo()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(6) { indice ->
                        val caracter =
                            codigo.getOrNull(indice)?.toString() ?: ""

                        Box(
                            modifier = Modifier
                                .size(width = 42.dp, height = 52.dp)
                                .background(
                                    VerdeSuperficie,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    BorderStroke(1.dp, Dorado),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = caracter,
                                color = Marfil,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    )
}