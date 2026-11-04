
package com.example.domino.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domino.ui.components.DominoButton
import com.example.domino.ui.viewmodel.LobbyViewModel

@Composable
fun CreateGameScreen(
    viewModel: LobbyViewModel,
    onGameCreated: (String) -> Unit,
    onBack: () -> Unit
) {
    val roomCode by viewModel.roomCode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Detecta cuando Firestore genera el código y activa la navegación
    LaunchedEffect(roomCode) {
        roomCode?.let { code ->
            if (code.isNotBlank()) {
                onGameCreated(code)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CREAR NUEVA PARTIDA",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Crea una sala e invita a tus amigos con su código.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            DominoButton(
                text = "Crear sala",
                onClick = { viewModel.createRoom() }
            )
        }

        error?.let { err ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error: $err",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DominoButton(
            text = "Volver al menú",
            onClick = onBack
        )
    }
}