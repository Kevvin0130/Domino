
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.Marfil
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo
import com.example.domino.ui.theme.VerdeSuperficie

@Composable
fun BienvenidaScreen(
    onIniciarSesion: () -> Unit,
    onCrearCuenta: () -> Unit,
    onGoogle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Sección superior: logo y presentación
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FichaLogo("3", "5")
                FichaLogo("6", "2")
                FichaLogo("4", "4")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "DOMINO",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Dorado,
                letterSpacing = 3.sp
            )

            Text(
                text = "CONQUEST",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Marfil,
                letterSpacing = 6.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "La estrategia conquista el tablero",
                color = TextoSecundario,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Sección de botones
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonDomino(
                texto = "Iniciar sesión",
                onClick = onIniciarSesion
            )

            BotonDomino(
                texto = "Crear cuenta",
                onClick = onCrearCuenta,
                principal = false
            )

            BotonDomino(
                texto = "Ingresar con Google",
                onClick = onGoogle,
                principal = false
            )
        }

        // Pie de la pantalla
        Text(
            text = "DOMINO CONQUEST • JUEGA CON ESTRATEGIA",
            color = TextoSecundario,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FichaLogo(
    arriba: String,
    abajo: String
) {
    Card(
        modifier = Modifier.size(width = 58.dp, height = 100.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Marfil
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = arriba,
                color = VerdeProfundo,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(VerdeSuperficie)
            )

            Text(
                text = abajo,
                color = VerdeProfundo,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}