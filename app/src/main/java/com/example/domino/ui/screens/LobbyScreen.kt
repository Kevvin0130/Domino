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

    LaunchedEffect(roomCode) {
        roomCode?.let { code ->
            onGameCreated(code)
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
            text = "Create New Game",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            DominoButton(
                text = "Generate Game Room",
                onClick = { viewModel.createRoom() }
            )
        }

        error?.let { err ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = err, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        DominoButton(
            text = "Back to Menu",
            onClick = onBack
        )
    }
}
