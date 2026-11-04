package com.example.domino.domain

import com.example.domino.domain.model.ActiveEvent
import com.example.domino.domain.model.Board
import com.example.domino.domain.model.EventType
import com.example.domino.domain.model.GamePhase
import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.Move
import com.example.domino.domain.model.Player
import com.example.domino.domain.model.PowerType
import com.example.domino.domain.model.Side
import com.example.domino.domain.model.Tile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {

    private val engine = GameEngineImpl()

    // region Helpers

    private fun player(
        id: String,
        hand: List<Tile>,
        score: Int = 0,
        powersUsed: Int = 0
    ) = Player(id = id, name = id.uppercase(), hand = hand, score = score, powersUsed = powersUsed)

    private fun game(
        players: List<Player>,
        board: List<Tile> = emptyList(),
        pile: List<Tile> = emptyList(),
        turn: Int = 0,
        direction: Int = 1
    ) = GameState(
        code = "TEST",
        hostId = players.first().id,
        phase = GamePhase.IN_PROGRESS,
        players = players,
        board = Board(board),
        drawPile = pile,
        currentTurn = turn,
        direction = direction
    )

    private fun ok(result: MoveResult): GameState {
        assertTrue("Expected Success but was $result", result is MoveResult.Success)
        return (result as MoveResult.Success).state
    }

    private fun rejected(result: MoveResult) {
        assertTrue("Expected Invalid but was $result", result is MoveResult.Invalid)
    }

    private fun startedGame(playerCount: Int = 2, seed: Int = 1): GameState {
        var state = engine.createGame("ROOM", Player(id = "p0", name = "P0"))
        for (i in 1 until playerCount) state = ok(engine.addPlayer(state, Player(id = "p$i", name = "P$i")))
        return ok(engine.start(state, Random(seed)))
    }

    // endregion

    // region Setup

    @Test
    fun start_dealsSevenTilesEachAndLeavesTheRestInThePile() {
        val state = startedGame(playerCount = 2)

        assertEquals(GamePhase.IN_PROGRESS, state.phase)
        state.players.forEach { assertEquals(7, it.hand.size) }
        assertEquals(14, state.drawPile.size)
        val all = state.players.flatMap { it.hand } + state.drawPile
        assertEquals(28, all.toSet().size)
    }

    @Test
    fun start_needsAtLeastTwoPlayers() {
        val state = engine.createGame("ROOM", Player(id = "a", name = "A"))
        rejected(engine.start(state, Random(1)))
    }

    @Test
    fun addPlayer_rejectsDuplicatesAndFullRooms() {
        var state = engine.createGame("ROOM", Player(id = "a", name = "A"))
        rejected(engine.addPlayer(state, Player(id = "a", name = "A again")))

        state = ok(engine.addPlayer(state, Player(id = "b", name = "B")))
        state = ok(engine.addPlayer(state, Player(id = "c", name = "C")))
        state = ok(engine.addPlayer(state, Player(id = "d", name = "D")))
        rejected(engine.addPlayer(state, Player(id = "e", name = "E")))
    }

    @Test
    fun addPlayer_isRejectedOnceTheGameStarted() {
        val state = startedGame()
        rejected(engine.addPlayer(state, Player(id = "late", name = "Late")))
    }

    // endregion

    // region Moves

    @Test
    fun firstMove_onEmptyBoardIsAccepted() {
        val state = game(
            players = listOf(player("a", listOf(Tile(1, 2), Tile(3, 3))), player("b", listOf(Tile(4, 5))))
        )

        val next = ok(engine.applyMove(state, Move("a", Tile(1, 2), Side.RIGHT)))

        assertEquals(listOf(Tile(1, 2)), next.board.chain)
        assertEquals(listOf(Tile(3, 3)), next.players[0].hand)
        assertEquals(3, next.players[0].score)
        assertEquals(1, next.currentTurn)
    }

    @Test
    fun move_thatDoesNotMatchIsRejected() {
        val state = game(
            players = listOf(player("a", listOf(Tile(1, 2), Tile(6, 6))), player("b", listOf(Tile(0, 0)))),
            board = listOf(Tile(3, 5))
        )
        rejected(engine.applyMove(state, Move("a", Tile(1, 2), Side.RIGHT)))
    }

    @Test
    fun move_isRejectedWhenItIsNotThePlayersTurn() {
        val state = game(
            players = listOf(player("a", listOf(Tile(1, 2))), player("b", listOf(Tile(2, 3)))),
            turn = 1
        )
        rejected(engine.applyMove(state, Move("a", Tile(1, 2), Side.RIGHT)))
    }

    @Test
    fun move_isRejectedWhenThePlayerDoesNotHoldTheTile() {
        val state = game(players = listOf(player("a", listOf(Tile(1, 2))), player("b", listOf(Tile(2, 3)))))
        rejected(engine.applyMove(state, Move("a", Tile(5, 5), Side.RIGHT)))
    }

    @Test
    fun placement_orientsTheTileToTouchTheChain() {
        val base = game(
            players = listOf(
                player("a", listOf(Tile(5, 1), Tile(1, 5), Tile(3, 2), Tile(0, 0))),
                player("b", listOf(Tile(4, 4)))
            ),
            board = listOf(Tile(3, 5))
        )

        val right = ok(engine.applyMove(base, Move("a", Tile(5, 1), Side.RIGHT)))
        assertEquals(Tile(5, 1), right.board.chain.last())

        val rightFlipped = ok(engine.applyMove(base, Move("a", Tile(1, 5), Side.RIGHT)))
        assertEquals(Tile(5, 1), rightFlipped.board.chain.last())

        val leftFlipped = ok(engine.applyMove(base, Move("a", Tile(3, 2), Side.LEFT)))
        assertEquals(Tile(2, 3), leftFlipped.board.chain.first())
        assertEquals(2, leftFlipped.board.leftEnd()) // the new open end
        assertEquals(5, leftFlipped.board.rightEnd())
    }

    @Test
    fun validMoves_listsOnlyMatchingTilesPlusAffordablePowers() {
        val state = game(
            players = listOf(player("a", listOf(Tile(3, 4), Tile(6, 6))), player("b", listOf(Tile(0, 0)))),
            board = listOf(Tile(2, 3))
        )

        val moves = engine.validMoves(state, "a")

        // Tile 3-4 matches the right end (3): normal + SKIP + REVERSE.
        val normal = moves.filter { it.power == null }
        assertEquals(listOf(Move("a", Tile(3, 4), Side.RIGHT)), normal)
        assertTrue(moves.any { it.power == PowerType.SKIP && it.tile == Tile(3, 4) })
        assertTrue(moves.any { it.power == PowerType.REVERSE && it.tile == Tile(3, 4) })
        // Tile 6-6 never matches: only as WILDCARD.
        assertTrue(moves.filter { it.tile == Tile(6, 6) }.all { it.power == PowerType.WILDCARD })
        // Nothing for a player who is not in turn.
        assertTrue(engine.validMoves(state, "b").isEmpty())
    }

    @Test
    fun round_increasesWhenTheTurnWrapsAround() {
        val state = game(
            players = listOf(
                player("a", listOf(Tile(3, 4), Tile(0, 0))),
                player("b", listOf(Tile(4, 5), Tile(6, 6)))
            ),
            board = listOf(Tile(2, 3))
        )

        val afterA = ok(engine.applyMove(state, Move("a", Tile(3, 4), Side.RIGHT)))
        assertEquals(1, afterA.round)
        val afterB = ok(engine.applyMove(afterA, Move("b", Tile(4, 5), Side.RIGHT)))
        assertEquals(0, afterB.currentTurn)
        assertEquals(2, afterB.round)
    }

    // endregion

    // region Powers

    @Test
    fun wildcard_allowsAnyTileAndConsumesAUse() {
        val state = game(
            players = listOf(player("a", listOf(Tile(5, 5), Tile(0, 0))), player("b", listOf(Tile(4, 4)))),
            board = listOf(Tile(2, 3))
        )

        rejected(engine.applyMove(state, Move("a", Tile(5, 5), Side.RIGHT)))

        val next = ok(engine.applyMove(state, Move("a", Tile(5, 5), Side.RIGHT, PowerType.WILDCARD)))
        assertEquals(Tile(5, 5), next.board.chain.last())
        assertEquals(1, next.players[0].usesLeft(PowerType.WILDCARD))
        assertEquals(1, next.players[0].powersUsed)
    }

    @Test
    fun power_withNoUsesLeftIsRejected() {
        val exhausted = player("a", listOf(Tile(5, 5), Tile(0, 0))).copy(powerUses = mapOf("WILDCARD" to 0))
        val state = game(players = listOf(exhausted, player("b", listOf(Tile(4, 4)))), board = listOf(Tile(2, 3)))

        rejected(engine.applyMove(state, Move("a", Tile(5, 5), Side.RIGHT, PowerType.WILDCARD)))
    }

    @Test
    fun skip_makesTheNextPlayerLoseTheirTurn() {
        val state = game(
            players = listOf(player("a", listOf(Tile(3, 4), Tile(0, 0))), player("b", listOf(Tile(4, 4)))),
            board = listOf(Tile(2, 3))
        )

        val next = ok(engine.applyMove(state, Move("a", Tile(3, 4), Side.RIGHT, PowerType.SKIP)))

        assertEquals(0, next.currentTurn) // b was skipped, a plays again
        assertNull(next.skippedPlayerId)
        assertEquals(1, next.players[0].usesLeft(PowerType.SKIP))
    }

    @Test
    fun reverse_changesTheTurnDirection() {
        val state = game(
            players = listOf(
                player("a", listOf(Tile(3, 4), Tile(0, 0))),
                player("b", listOf(Tile(4, 4))),
                player("c", listOf(Tile(5, 5)))
            ),
            board = listOf(Tile(2, 3))
        )

        val next = ok(engine.applyMove(state, Move("a", Tile(3, 4), Side.RIGHT, PowerType.REVERSE)))

        assertEquals(-1, next.direction)
        assertEquals(2, next.currentTurn) // goes to c, not b
    }

    @Test
    fun swap_exchangesTilesWithAnOpponentAndKeepsTheBoard() {
        val state = game(
            players = listOf(
                player("a", listOf(Tile(1, 1), Tile(2, 2))),
                player("b", listOf(Tile(3, 3), Tile(4, 4)))
            ),
            board = listOf(Tile(0, 0))
        )

        val next = ok(
            engine.applyMove(
                state,
                Move("a", Tile(1, 1), power = PowerType.SWAP, targetId = "b", targetTile = Tile(4, 4))
            )
        )

        assertTrue(Tile(4, 4) in next.players[0].hand)
        assertFalse(Tile(1, 1) in next.players[0].hand)
        assertTrue(Tile(1, 1) in next.players[1].hand)
        assertFalse(Tile(4, 4) in next.players[1].hand)
        assertEquals(state.board, next.board)
        assertEquals(1, next.currentTurn)
    }

    @Test
    fun swap_rejectsInvalidTargets() {
        val state = game(
            players = listOf(player("a", listOf(Tile(1, 1))), player("b", listOf(Tile(3, 3)))),
            board = listOf(Tile(0, 0))
        )
        rejected(engine.applyMove(state, Move("a", Tile(1, 1), power = PowerType.SWAP, targetId = "a", targetTile = Tile(1, 1))))
        rejected(engine.applyMove(state, Move("a", Tile(1, 1), power = PowerType.SWAP, targetId = "b", targetTile = Tile(6, 6))))
        rejected(engine.applyMove(state, Move("a", Tile(1, 1), power = PowerType.SWAP)))
    }

    // endregion

    // region Drawing

    @Test
    fun draw_isRejectedWhenThePlayerHasAValidMove() {
        val state = game(
            players = listOf(player("a", listOf(Tile(1, 5))), player("b", listOf(Tile(0, 0)))),
            board = listOf(Tile(0, 5)),
            pile = listOf(Tile(2, 2))
        )
        rejected(engine.drawFromPile(state, "a"))
    }

    @Test
    fun draw_takesOneTileAndKeepsTheTurn() {
        val state = game(
            players = listOf(player("a", listOf(Tile(6, 6))), player("b", listOf(Tile(0, 0)))),
            board = listOf(Tile(0, 1)),
            pile = listOf(Tile(1, 1), Tile(2, 2))
        )

        val next = ok(engine.drawFromPile(state, "a"))

        assertEquals(2, next.players[0].hand.size)
        assertTrue(Tile(1, 1) in next.players[0].hand)
        assertEquals(listOf(Tile(2, 2)), next.drawPile)
        assertEquals(0, next.currentTurn)
    }

    @Test
    fun draw_withAnEmptyPileLosesTheTurn() {
        val state = game(
            players = listOf(player("a", listOf(Tile(6, 6))), player("b", listOf(Tile(0, 0)))),
            board = listOf(Tile(0, 1))
        )

        val next = ok(engine.drawFromPile(state, "a"))

        assertEquals(1, next.currentTurn)
        assertEquals(GamePhase.IN_PROGRESS, next.phase) // players still have wildcards
    }

    // endregion

    // region End of game

    @Test
    fun goingOut_endsTheGameWithABonusFromTheOpponentsTiles() {
        val state = game(
            players = listOf(
                player("a", listOf(Tile(3, 4))),
                player("b", listOf(Tile(5, 5), Tile(1, 2)))
            ),
            board = listOf(Tile(2, 3))
        )

        val end = ok(engine.applyMove(state, Move("a", Tile(3, 4), Side.RIGHT)))

        assertEquals(GamePhase.FINISHED, end.phase)
        assertEquals("a", end.winnerId)
        assertEquals(7 + 13, end.players[0].score) // 3+4 pips played + (10 + 3) left in b's hand
    }

    @Test
    fun blockedGame_endsAndTheHighestScoreWins() {
        val noPowers = emptyMap<String, Int>()
        val state = game(
            players = listOf(
                player("a", listOf(Tile(6, 6)), score = 30).copy(powerUses = noPowers),
                player("b", listOf(Tile(2, 2)), score = 10).copy(powerUses = noPowers)
            ),
            board = listOf(Tile(0, 1))
        )

        val end = ok(engine.drawFromPile(state, "a")) // empty pile: a passes, nobody can move

        assertEquals(GamePhase.FINISHED, end.phase)
        assertEquals("a", end.winnerId)
    }

    @Test
    fun tie_isBrokenByFewerPowersUsed_andIsNullIfStillTied() {
        val exceeded = game(
            players = listOf(
                player("a", listOf(Tile(1, 1)), score = 10, powersUsed = 2),
                player("b", listOf(Tile(2, 2)), score = 10, powersUsed = 1)
            )
        ).copy(round = 31, maxRounds = 30)

        val byPowers = engine.evaluateEnd(exceeded)
        assertEquals(GamePhase.FINISHED, byPowers.phase)
        assertEquals("b", byPowers.winnerId)

        val tied = engine.evaluateEnd(
            exceeded.copy(players = exceeded.players.map { it.copy(powersUsed = 1) })
        )
        assertEquals(GamePhase.FINISHED, tied.phase)
        assertNull(tied.winnerId)
    }

    @Test
    fun evaluateEnd_doesNothingWhileTheGameCanContinue() {
        val state = game(
            players = listOf(player("a", listOf(Tile(3, 4))), player("b", listOf(Tile(4, 4)))),
            board = listOf(Tile(2, 3))
        )
        assertEquals(state, engine.evaluateEnd(state))
    }

    // endregion

    // region Abandon and restart

    @Test
    fun abandon_inProgressWithTwoPlayersEndsTheGameForTheOther() {
        val state = game(players = listOf(player("a", listOf(Tile(1, 1))), player("b", listOf(Tile(2, 2)))))

        val end = engine.abandon(state, "a")

        assertEquals(GamePhase.FINISHED, end.phase)
        assertEquals("b", end.winnerId)
    }

    @Test
    fun abandon_withThreePlayersPassesTheTurnWhenItWasTheirs() {
        val state = game(
            players = listOf(
                player("a", listOf(Tile(1, 1))),
                player("b", listOf(Tile(2, 2))),
                player("c", listOf(Tile(3, 3)))
            )
        )

        val next = engine.abandon(state, "a")

        assertEquals(GamePhase.IN_PROGRESS, next.phase)
        assertEquals(1, next.currentTurn)
        assertTrue(next.players[0].abandoned)
    }

    @Test
    fun abandon_inTheWaitingRoomRemovesThePlayerAndReassignsTheHost() {
        var state = engine.createGame("ROOM", Player(id = "a", name = "A"))
        state = ok(engine.addPlayer(state, Player(id = "b", name = "B")))

        val next = engine.abandon(state, "a")

        assertEquals(listOf("b"), next.players.map { it.id })
        assertEquals("b", next.hostId)
    }

    @Test
    fun restart_resetsEverythingAndDealsAgain() {
        val finished = startedGame().let { started ->
            started.copy(
                phase = GamePhase.FINISHED,
                winnerId = "p0",
                round = 9,
                players = started.players.map { it.copy(score = 50, powersUsed = 3) }
            )
        }

        val next = engine.restart(finished, Random(3))

        assertEquals(GamePhase.IN_PROGRESS, next.phase)
        assertEquals(1, next.round)
        assertNull(next.winnerId)
        next.players.forEach {
            assertEquals(0, it.score)
            assertEquals(0, it.powersUsed)
            assertEquals(7, it.hand.size)
        }
    }

    // endregion

    // region Events

    @Test
    fun doubleScore_doublesThePointsWhileActive() {
        val state = game(players = listOf(player("a", listOf(Tile(3, 4))), player("b", listOf(Tile(1, 1)))))

        assertEquals(7, engine.calculatePoints(state, Tile(3, 4)))
        val active = state.copy(activeEvent = ActiveEvent(EventType.DOUBLE_SCORE, 2))
        assertEquals(14, engine.calculatePoints(active, Tile(3, 4)))
        val expired = state.copy(activeEvent = ActiveEvent(EventType.DOUBLE_SCORE, 0))
        assertEquals(7, engine.calculatePoints(expired, Tile(3, 4)))
    }

    @Test
    fun doubleScore_expiresAfterItsRounds() {
        val state = game(
            players = listOf(
                player("a", listOf(Tile(3, 4), Tile(0, 0), Tile(6, 6))),
                player("b", listOf(Tile(4, 5), Tile(6, 6), Tile(1, 1)))
            ),
            board = listOf(Tile(2, 3))
        ).copy(activeEvent = ActiveEvent(EventType.DOUBLE_SCORE, 1))

        val afterA = ok(engine.applyMove(state, Move("a", Tile(3, 4), Side.RIGHT)))
        assertEquals(14, afterA.players[0].score) // doubled
        val afterB = ok(engine.applyMove(afterA, Move("b", Tile(4, 5), Side.RIGHT)))
        assertEquals(0, afterB.activeEvent?.roundsLeft) // a full round passed
    }

    @Test
    fun tileStorm_keepsHandSizesAndAllTheTiles() {
        val state = startedGame(playerCount = 3)

        val next = engine.applyEvent(state, EventType.TILE_STORM, Random(5))

        assertEquals(state.players.map { it.hand.size }, next.players.map { it.hand.size })
        assertEquals(state.drawPile.size, next.drawPile.size)
        val all = next.players.flatMap { it.hand } + next.drawPile
        assertEquals(28, all.toSet().size)
        assertEquals(EventType.TILE_STORM, next.activeEvent?.type)
    }

    @Test
    fun forcedDraw_givesTheCurrentPlayerOneTile() {
        val state = game(
            players = listOf(player("a", listOf(Tile(1, 1))), player("b", listOf(Tile(2, 2)))),
            pile = listOf(Tile(6, 6))
        )

        val next = engine.applyEvent(state, EventType.FORCED_DRAW)

        assertEquals(2, next.players[0].hand.size)
        assertTrue(next.drawPile.isEmpty())
    }

    @Test
    fun turnReversal_flipsTheDirection() {
        val state = game(players = listOf(player("a", listOf(Tile(1, 1))), player("b", listOf(Tile(2, 2)))))
        assertEquals(-1, engine.applyEvent(state, EventType.TURN_REVERSAL).direction)
    }

    @Test
    fun randomEvent_neverTriggersOutsideAGameOrWhileAnotherIsActive() {
        val waiting = game(players = listOf(player("a", listOf(Tile(1, 1))), player("b", listOf(Tile(2, 2)))))
            .copy(phase = GamePhase.WAITING)
        assertEquals(waiting, engine.applyRandomEvent(waiting, Random(1)))

        val busy = game(players = listOf(player("a", listOf(Tile(1, 1))), player("b", listOf(Tile(2, 2)))))
            .copy(activeEvent = ActiveEvent(EventType.DOUBLE_SCORE, 2))
        repeat(50) { assertEquals(busy, engine.applyRandomEvent(busy, Random(it))) }
    }

    @Test
    fun randomEvent_triggersSometimes() {
        val state = startedGame()
        val triggered = (0 until 200).count { engine.applyRandomEvent(state, Random(it)) != state }
        assertTrue("Expected some events out of 200 tries but got $triggered", triggered in 20..100)
    }

    // endregion
}