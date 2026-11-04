package com.example.domino.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.TextoSecundario
import com.example.domino.ui.theme.VerdeProfundo

/** Muestra [mensaje] como Toast una sola vez y avisa con [onMostrado] para limpiarlo en el ViewModel. */
@Composable
fun MensajeEffect(mensaje: String?, onMostrado: () -> Unit) {
    val contexto = LocalContext.current
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show()
            onMostrado()
        }
    }
}

/** Pantalla de espera mientras llega el estado de Firestore. Siempre deja una salida. */
@Composable
fun PantallaCargando(texto: String, onSalir: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = Dorado)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = texto, color = TextoSecundario)
        Spacer(modifier = Modifier.height(24.dp))
        BotonDomino(texto = "Volver al menú", onClick = onSalir, principal = false)
    }
}
