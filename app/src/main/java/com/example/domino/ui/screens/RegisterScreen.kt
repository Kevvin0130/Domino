
package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.domino.ui.components.BotonDomino
import com.example.domino.ui.theme.Dorado
import com.example.domino.ui.theme.RojoTrampa
import com.example.domino.ui.theme.VerdeProfundo

@Composable
fun RegistroScreen(
    onRegistrar: (String, String, String) -> Unit,
    onIrIniciarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    var nombreUsuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeProfundo)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CREAR CUENTA",
            style = MaterialTheme.typography.headlineLarge,
            color = Dorado,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            label = { Text("Nombre de usuario") },
            singleLine = true, //Mantiene el campo en una sola linea
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electronico") },
            singleLine = true, //Mantiene el campo en una sola linea
            modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                    )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            singleLine = true, //Mantiene el campo en una sola linea
            modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            label = { Text("Confrimar Contraseña") },
            singleLine = true, //Mantiene el campo en una sola linea
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )
        Spacer(modifier = Modifier.height(18.dp))

        BotonDomino(
            texto = "Registrarse",
            onClick = {
                mensajeError = when {
                    nombreUsuario.isBlank() ->
                        "Escribe un nombre de jugador"

                    correo.isBlank() || !correo.contains("@") ->
                        "Escribe un correo válido"

                    contrasena.length < 6 ->
                        "La contraseña debe tener al menos 6 caracteres"

                    contrasena != confirmarContrasena ->
                        "Las contraseñas no coinciden"

                    else -> ""
                }

                if (mensajeError.isEmpty()) {
                    onRegistrar(
                        nombreUsuario.trim(),
                        correo.trim(),
                        contrasena
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            altura = 60.dp
        )
        if (mensajeError.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = mensajeError,
                color = RojoTrampa
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
            BotonDomino(
                texto = "Volver",
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth(),
                altura = 45.dp
            )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "¿Ya tienes una cuenta? Inicia sesión",
            color = Dorado,
            modifier = Modifier.clickable {
                onIrIniciarSesion()
            }
        )
    }
}