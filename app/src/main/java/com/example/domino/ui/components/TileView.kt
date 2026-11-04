package com.example.domino.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.domino.domain.model.Tile

@Composable
fun TileView(
    tile: Tile,
    onClick: (() -> Unit)? = null,
    isHorizontal: Boolean = false,
    modifier: Modifier = Modifier
) {
    FichaView(
        ficha = tile,
        modifier = modifier,
        horizontal = isHorizontal,
        onClick = onClick
    )
}
