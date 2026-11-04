package com.example.domino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.domain.model.Tile

@Composable
fun TileView(
    tile: Tile,
    onClick: (() -> Unit)? = null,
    isHorizontal: Boolean = false,
    modifier: Modifier = Modifier
) {
    val boxModifier = modifier
        .then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        )
        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
        .background(Color.White, RoundedCornerShape(8.dp))
        .padding(4.dp)

    if (isHorizontal) {
        Row(
            modifier = boxModifier
                .height(40.dp)
                .width(80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = tile.sideA.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.Gray))
            Text(text = tile.sideB.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    } else {
        Column(
            modifier = boxModifier
                .width(40.dp)
                .height(80.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = tile.sideA.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Box(modifier = Modifier.height(1.dp).width(30.dp).background(Color.Gray))
            Text(text = tile.sideB.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}
