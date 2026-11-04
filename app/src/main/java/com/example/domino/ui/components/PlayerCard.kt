package com.example.domino.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domino.domain.model.Player

@Composable
fun PlayerCard(
    player: Player,
    isCurrentTurn: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isCurrentTurn) Modifier.border(2.dp, Color.Green, RoundedCornerShape(12.dp)) else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Score: ${player.score}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tiles left: ${player.hand.size}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}