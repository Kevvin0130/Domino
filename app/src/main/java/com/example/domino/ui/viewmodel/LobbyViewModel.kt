package com.example.domino.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domino.ui.components.DominoButton
import com.example.domino.ui.viewmodel.LobbyViewModel

@Composable
fun JoinGameScreen(
    viewModel: LobbyViewModel,
    onJoined: (String) -> Unit,
    onBack: () -> Unit
) {
    var codeInput by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Join Game Room",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = codeInput,
            onValueChange = { codeInput = it.uppercase() },
            label = { Text("Room Code") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            DominoButton(
                text = "Join Room",
                enabled = codeInput.isNotBlank(),
                onClick = {
                    viewModel.joinRoom(codeInput) { success ->
                        if (success) onJoined(codeInput)
                    }
                }
            )
        }

        error?.let { err ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = err, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        DominoButton(
            text = "Back",
            onClick = onBack
        )
    }
}