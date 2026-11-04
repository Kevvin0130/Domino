package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.RojoTrampa
import com.example.domino.ui.theme.VerdeProfundo

@Composable
fun RecuperarContrasenaScreen(
    onRecuperar: (String) -> Unit,
    onVolver: () -> Unit
){
    var correo by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = "RECUPERAR CONTRASEÑA",
            style = MaterialTheme.typography.headlineLarge,
            color = Dorado,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo vinculado a la cuenta") },
            singleLine = true, //Mantiene el campo en una sola linea
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )
        if (mensajeError.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = mensajeError,
                color = RojoTrampa
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        BotonDomino(
            texto = "Recuperar contraseña",
            onClick = {
                mensajeError = when {
                    correo.isBlank() || !correo.contains("@") ->
                        "Escribe un correo válido"
                    else -> ""
                }
                if (mensajeError.isEmpty()) {
                    onRecuperar(
                        correo.trim()
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            altura = 50.dp
        )
        Spacer(modifier = Modifier.height(12.dp))
        BotonDomino(
            texto = "Volver",
            onClick = onVolver,
            altura = 50.dp
        )

    }
}