package com.example.domino.domain

import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.Move
import com.example.domino.domain.model.Player
import com.example.domino.domain.model.Tile
import kotlin.random.Random

/**
 * PERSON A: implement tasks A1–A5 here. Each function has its TODO.
 * Until then the app compiles, but the real game does not work.
 */
class GameEngineImpl : GameEngine {
    override fun createGame(code: String, host: Player): GameState = TODO("A1")
    override fun addPlayer(state: GameState, player: Player): MoveResult = TODO("A1")
    override fun start(state: GameState, random: Random): MoveResult = TODO("A1")
    override fun validMoves(state: GameState, playerId: String): List<Move> = TODO("A1")
    override fun applyMove(state: GameState, move: Move): MoveResult = TODO("A1-A3")
    override fun drawFromPile(state: GameState, playerId: String): MoveResult = TODO("A2")
    override fun applyRandomEvent(state: GameState, random: Random): GameState = TODO("A4")
    override fun abandon(state: GameState, playerId: String): GameState = TODO("A5")
    override fun restart(state: GameState, random: Random): GameState = TODO("A5")
    override fun evaluateEnd(state: GameState): GameState = TODO("A5")
    override fun calculatePoints(state: GameState, tile: Tile): Int = TODO("A4")
}