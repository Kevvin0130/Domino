package com.example.domino.domain.sample

import com.example.domino.domain.model.*

/**
 * PERSON B: fake data to build and preview the UI (@Preview)
 * without depending on the engine or Firebase.
 */
object SampleState {
    val inProgressGame = GameState(
        code = "A7K3B9",
        hostId = "u1",
        phase = GamePhase.IN_PROGRESS,
        players = listOf(
            Player(
                id = "u1", name = "Jorge", score = 1250,
                hand = listOf(Tile(6, 6), Tile(3, 4), Tile(0, 2), Tile(5, 1), Tile(2, 2), Tile(4, 4), Tile(1, 3))
            ),
            Player(
                id = "u2", name = "Ana", score = 980,
                hand = listOf(Tile(1, 1), Tile(5, 6), Tile(0, 3), Tile(2, 4), Tile(3, 3), Tile(0, 6), Tile(5, 5))
            )
        ),
        board = Board(listOf(Tile(2, 3), Tile(3, 6), Tile(6, 1))),
        drawPile = listOf(Tile(0, 0), Tile(0, 1), Tile(4, 5), Tile(1, 4)),
        currentTurn = 0,
        round = 5,
        lastMessage = "Ana placed 6-1"
    )
}