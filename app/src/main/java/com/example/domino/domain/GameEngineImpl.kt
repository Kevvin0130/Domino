package com.example.domino.domain

import com.example.domino.domain.model.ActiveEvent
import com.example.domino.domain.model.Board
import com.example.domino.domain.model.EventType
import com.example.domino.domain.model.GamePhase
import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.INITIAL_USES_PER_POWER
import com.example.domino.domain.model.Move
import com.example.domino.domain.model.Player
import com.example.domino.domain.model.PowerType
import com.example.domino.domain.model.Side
import com.example.domino.domain.model.Tile
import kotlin.random.Random

/**
 * Game rules implementation (tasks A1–A5). Pure Kotlin, no Android imports.
 * Every function returns a NEW state; nothing is mutated.
 *
 * Rules summary:
 *  - 2 to 4 players, 7 tiles each, the rest forms the draw pile.
 *  - A normal move needs the tile to match the open end of the chain.
 *  - Powers (each has limited uses per player):
 *      WILDCARD  place a tile ignoring the end it should match.
 *      SKIP      normal move + the next player loses their turn.
 *      REVERSE   normal move + the turn direction changes.
 *      SWAP      no placement: swap one of your tiles with one of an opponent's.
 *    A power is chosen BEFORE the move, in [Move.power].
 *  - Without a normal move the player draws one tile at a time and keeps the turn;
 *    with an empty pile the turn is lost.
 *  - The game ends when a player runs out of tiles, nobody can move, the maximum
 *    number of rounds is exceeded, or only one player is left.
 *  - Winner: highest score; tie -> fewer powers used; still tied -> no winner (null).
 */
class GameEngineImpl : GameEngine {

    // region Setup (A1)

    override fun createGame(code: String, host: Player): GameState =
        GameState(
            code = code,
            hostId = host.id,
            phase = GamePhase.WAITING,
            players = listOf(host)
        )

    override fun addPlayer(state: GameState, player: Player): MoveResult = when {
        state.phase != GamePhase.WAITING -> MoveResult.Invalid("The game has already started")
        state.players.size >= MAX_PLAYERS -> MoveResult.Invalid("The room is full")
        state.players.any { it.id == player.id } -> MoveResult.Invalid("You are already in this room")
        else -> MoveResult.Success(state.copy(players = state.players + player))
    }

    override fun start(state: GameState, random: Random): MoveResult {
        if (state.phase == GamePhase.IN_PROGRESS) return MoveResult.Invalid("The game is already in progress")
        if (state.players.size < MIN_PLAYERS) return MoveResult.Invalid("At least $MIN_PLAYERS players are needed")

        val deck = fullSet().shuffled(random)
        val players = state.players.mapIndexed { index, player ->
            player.copy(
                hand = deck.subList(index * HAND_SIZE, (index + 1) * HAND_SIZE).toList(),
                score = 0,
                powerUses = freshPowerUses(),
                powersUsed = 0,
                abandoned = false
            )
        }
        val first = startingPlayerIndex(players)
        return MoveResult.Success(
            state.copy(
                phase = GamePhase.IN_PROGRESS,
                players = players,
                board = Board(),
                drawPile = deck.drop(players.size * HAND_SIZE),
                currentTurn = first,
                direction = 1,
                round = 1,
                activeEvent = null,
                skippedPlayerId = null,
                winnerId = null,
                lastMessage = "${players[first].name} starts"
            )
        )
    }

    // endregion

    // region Moves (A1–A3)

    /**
     * All valid moves of the player whose turn it is: normal moves plus the
     * variants with WILDCARD, SKIP and REVERSE that the player can still afford.
     * SWAP moves are NOT listed (they depend on the opponent's tile): the UI
     * builds them and [applyMove] validates them.
     */
    override fun validMoves(state: GameState, playerId: String): List<Move> {
        val player = playerToAct(state, playerId) ?: return emptyList()
        val moves = mutableListOf<Move>()
        for (tile in player.hand) {
            for (side in Side.entries) {
                // On an empty board both sides are the same: offer a single option.
                if (state.board.isEmpty() && side == Side.LEFT) continue
                if (fits(state.board, tile, side)) {
                    moves += Move(playerId, tile, side)
                    if (player.usesLeft(PowerType.SKIP) > 0) moves += Move(playerId, tile, side, PowerType.SKIP)
                    if (player.usesLeft(PowerType.REVERSE) > 0) moves += Move(playerId, tile, side, PowerType.REVERSE)
                } else if (player.usesLeft(PowerType.WILDCARD) > 0) {
                    moves += Move(playerId, tile, side, PowerType.WILDCARD)
                }
            }
        }
        return moves
    }

    override fun applyMove(state: GameState, move: Move): MoveResult {
        if (state.phase != GamePhase.IN_PROGRESS) return MoveResult.Invalid("The game is not in progress")
        val index = state.players.indexOfFirst { it.id == move.playerId }
        val player = state.players.getOrNull(index) ?: return MoveResult.Invalid("Unknown player")
        if (player.abandoned || index != state.currentTurn) return MoveResult.Invalid("It is not your turn")
        if (move.tile !in player.hand) return MoveResult.Invalid("You do not have that tile")
        val power = move.power
        if (power != null && player.usesLeft(power) <= 0) return MoveResult.Invalid("No uses left for $power")

        return if (power == PowerType.SWAP) applySwap(state, index, move) else applyPlacement(state, index, move)
    }

    private fun applyPlacement(state: GameState, index: Int, move: Move): MoveResult {
        val player = state.players[index]
        val power = move.power
        if (!fits(state.board, move.tile, move.side) && power != PowerType.WILDCARD) {
            return MoveResult.Invalid("That tile does not match the end of the board")
        }

        val placed = orient(state.board, move.tile, move.side)
        val chain = state.board.chain
        val newChain = if (move.side == Side.LEFT) listOf(placed) + chain else chain + placed
        val points = calculatePoints(state, move.tile)

        var direction = state.direction
        var skipped = state.skippedPlayerId
        when (power) {
            PowerType.REVERSE -> direction = -direction
            PowerType.SKIP -> skipped = nextActivePlayerId(state, index)
            else -> Unit
        }

        val updated = player
            .copy(hand = player.hand - move.tile, score = player.score + points)
            .spend(power)
        val message = when (power) {
            null -> "${player.name} placed ${placed.left}-${placed.right}"
            else -> "${player.name} used ${power.label()} and placed ${placed.left}-${placed.right}"
        }

        return MoveResult.Success(
            finishTurn(
                state.copy(
                    players = state.players.replaceAt(index, updated),
                    board = Board(newChain),
                    direction = direction,
                    skippedPlayerId = skipped,
                    lastMessage = message
                )
            )
        )
    }

    private fun applySwap(state: GameState, index: Int, move: Move): MoveResult {
        val player = state.players[index]
        val targetIndex = state.players.indexOfFirst { it.id == move.targetId }
        val target = state.players.getOrNull(targetIndex) ?: return MoveResult.Invalid("Choose an opponent")
        if (targetIndex == index || target.abandoned) return MoveResult.Invalid("Invalid swap target")
        val targetTile = move.targetTile ?: return MoveResult.Invalid("Choose the opponent's tile")
        if (targetTile !in target.hand) return MoveResult.Invalid("The opponent does not have that tile")

        val me = player.copy(hand = player.hand - move.tile + targetTile).spend(PowerType.SWAP)
        val other = target.copy(hand = target.hand - targetTile + move.tile)
        val players = state.players.replaceAt(index, me).replaceAt(targetIndex, other)

        return MoveResult.Success(
            finishTurn(
                state.copy(
                    players = players,
                    lastMessage = "${player.name} swapped a tile with ${target.name}"
                )
            )
        )
    }

    override fun drawFromPile(state: GameState, playerId: String): MoveResult {
        val player = playerToAct(state, playerId) ?: return MoveResult.Invalid("It is not your turn")
        if (hasNormalMove(state.board, player)) {
            return MoveResult.Invalid("You have a valid move, you cannot draw")
        }
        val index = state.currentTurn

        if (state.drawPile.isNotEmpty()) {
            // The player draws one tile and keeps the turn (may draw again or play).
            val updated = player.copy(hand = player.hand + state.drawPile.first())
            return MoveResult.Success(
                state.copy(
                    players = state.players.replaceAt(index, updated),
                    drawPile = state.drawPile.drop(1),
                    lastMessage = "${player.name} drew a tile"
                )
            )
        }
        // Empty pile and no move: the turn is lost.
        return MoveResult.Success(finishTurn(state.copy(lastMessage = "${player.name} passed")))
    }

    /** Points a tile is worth: its pips, doubled while DOUBLE_SCORE is active. */
    override fun calculatePoints(state: GameState, tile: Tile): Int {
        val event = state.activeEvent
        val doubled = event != null && event.type == EventType.DOUBLE_SCORE && event.roundsLeft > 0
        return if (doubled) tile.points() * 2 else tile.points()
    }

    // endregion

    // region Events (A4)

    /** With probability [EVENT_PROBABILITY] triggers a random event (never while another lasts). */
    override fun applyRandomEvent(state: GameState, random: Random): GameState {
        if (state.phase != GamePhase.IN_PROGRESS) return state
        if ((state.activeEvent?.roundsLeft ?: 0) > 0) return state
        if (random.nextDouble() >= EVENT_PROBABILITY) return state
        return applyEvent(state, EventType.entries.random(random), random)
    }

    /** Applies a specific event. Public so it can be tested deterministically. */
    fun applyEvent(state: GameState, type: EventType, random: Random = Random.Default): GameState =
        when (type) {
            EventType.TILE_STORM -> {
                // Everyone's tiles go back to the pool and are dealt again, keeping hand sizes.
                val pool = (state.players.flatMap { if (it.abandoned) emptyList() else it.hand } + state.drawPile)
                    .shuffled(random)
                var cursor = 0
                val players = state.players.map { player ->
                    if (player.abandoned) {
                        player
                    } else {
                        val newHand = pool.subList(cursor, cursor + player.hand.size).toList()
                        cursor += player.hand.size
                        player.copy(hand = newHand)
                    }
                }
                state.copy(
                    players = players,
                    drawPile = pool.drop(cursor),
                    activeEvent = ActiveEvent(type, 0),
                    lastMessage = "Tile storm! Everyone's tiles were reshuffled"
                )
            }
            EventType.DOUBLE_SCORE -> state.copy(
                activeEvent = ActiveEvent(type, 2),
                lastMessage = "Double score for 2 rounds!"
            )
            EventType.FORCED_DRAW -> {
                val player = state.currentPlayer()
                if (player != null && state.drawPile.isNotEmpty()) {
                    state.copy(
                        players = state.players.replaceAt(
                            state.currentTurn,
                            player.copy(hand = player.hand + state.drawPile.first())
                        ),
                        drawPile = state.drawPile.drop(1),
                        activeEvent = ActiveEvent(type, 0),
                        lastMessage = "Forced draw! ${player.name} takes a tile"
                    )
                } else {
                    state.copy(activeEvent = ActiveEvent(type, 0), lastMessage = "Forced draw, but the pile is empty")
                }
            }
            EventType.TURN_REVERSAL -> state.copy(
                direction = -state.direction,
                activeEvent = ActiveEvent(type, 0),
                lastMessage = "Turn order reversed!"
            )
        }

    // endregion

    // region End of game, abandon, restart (A5)

    override fun evaluateEnd(state: GameState): GameState {
        if (state.phase != GamePhase.IN_PROGRESS) return state

        val active = state.players.filter { !it.abandoned }
        val outPlayer = active.firstOrNull { it.hand.isEmpty() }
        val blocked = state.drawPile.isEmpty() && active.none { canMoveAtAll(state, it) }
        val roundsOver = state.round > state.maxRounds
        if (outPlayer == null && !blocked && !roundsOver) return state

        var players = state.players
        if (outPlayer != null) {
            // Bonus for going out: the pips left in the opponents' hands.
            val bonus = active.filter { it.id != outPlayer.id }.sumOf { p -> p.hand.sumOf { it.points() } }
            players = players.map { if (it.id == outPlayer.id) it.copy(score = it.score + bonus) else it }
        }
        val winner = decideWinner(players)
        val message = when {
            outPlayer != null -> "${outPlayer.name} ran out of tiles"
            blocked -> "Nobody can move"
            else -> "Maximum number of rounds reached"
        }
        return state.copy(
            phase = GamePhase.FINISHED,
            players = players,
            winnerId = winner?.id,
            lastMessage = message
        )
    }

    override fun abandon(state: GameState, playerId: String): GameState {
        val index = state.players.indexOfFirst { it.id == playerId }
        if (index < 0) return state
        val player = state.players[index]

        return when (state.phase) {
            GamePhase.WAITING -> {
                val rest = state.players.filterNot { it.id == playerId }
                val host = if (state.hostId == playerId) rest.firstOrNull()?.id ?: "" else state.hostId
                state.copy(players = rest, hostId = host)
            }
            GamePhase.FINISHED -> state.copy(
                players = state.players.replaceAt(index, player.copy(abandoned = true))
            )
            GamePhase.IN_PROGRESS -> {
                val marked = state.copy(
                    players = state.players.replaceAt(index, player.copy(abandoned = true)),
                    lastMessage = "${player.name} left the game"
                )
                val remaining = marked.players.filter { !it.abandoned }
                when {
                    remaining.size <= 1 -> marked.copy(
                        phase = GamePhase.FINISHED,
                        winnerId = remaining.firstOrNull()?.id
                    )
                    index == state.currentTurn -> evaluateEnd(advanceTurn(marked))
                    else -> evaluateEnd(marked)
                }
            }
        }
    }

    /** Same players (without those who left), everything reset; starts at once if there are enough. */
    override fun restart(state: GameState, random: Random): GameState {
        val players = state.players.filter { !it.abandoned }.map {
            it.copy(hand = emptyList(), score = 0, powerUses = freshPowerUses(), powersUsed = 0, abandoned = false)
        }
        val waiting = state.copy(
            phase = GamePhase.WAITING,
            hostId = if (players.any { it.id == state.hostId }) state.hostId else players.firstOrNull()?.id ?: "",
            players = players,
            board = Board(),
            drawPile = emptyList(),
            currentTurn = 0,
            direction = 1,
            round = 1,
            activeEvent = null,
            skippedPlayerId = null,
            winnerId = null,
            lastMessage = ""
        )
        if (players.size < MIN_PLAYERS) return waiting
        return (start(waiting, random) as MoveResult.Success).state
    }

    // endregion

    // region Helpers

    /** The player only if it is their turn, the game is running and they have not left. */
    private fun playerToAct(state: GameState, playerId: String): Player? {
        if (state.phase != GamePhase.IN_PROGRESS) return null
        val player = state.currentPlayer() ?: return null
        return player.takeIf { it.id == playerId && !it.abandoned }
    }

    /** True if [tile] can be placed on [side] without any power. */
    private fun fits(board: Board, tile: Tile, side: Side): Boolean {
        if (board.isEmpty()) return true
        val end = if (side == Side.LEFT) board.leftEnd() else board.rightEnd()
        return end != null && tile.contains(end)
    }

    /** Orients the tile so its inner half touches the chain; unchanged if it does not match (wildcard). */
    private fun orient(board: Board, tile: Tile, side: Side): Tile {
        if (board.isEmpty()) return tile
        return if (side == Side.LEFT) {
            val end = board.leftEnd()
            when (end) {
                tile.right -> tile
                tile.left -> tile.flipped()
                else -> tile
            }
        } else {
            val end = board.rightEnd()
            when (end) {
                tile.left -> tile
                tile.right -> tile.flipped()
                else -> tile
            }
        }
    }

    private fun hasNormalMove(board: Board, player: Player): Boolean =
        player.hand.any { tile -> Side.entries.any { fits(board, tile, it) } }

    /** Can the player do anything at all (normal move or an available power)? Used to detect a blocked game. */
    private fun canMoveAtAll(state: GameState, player: Player): Boolean {
        if (player.hand.isEmpty()) return false
        if (hasNormalMove(state.board, player)) return true
        if (player.usesLeft(PowerType.WILDCARD) > 0) return true
        return player.usesLeft(PowerType.SWAP) > 0 &&
                state.players.any { it.id != player.id && !it.abandoned && it.hand.isNotEmpty() }
    }

    /** Ends the turn: checks for the end of the game, passes the turn and checks again. */
    private fun finishTurn(state: GameState): GameState {
        val checked = evaluateEnd(state)
        if (checked.phase == GamePhase.FINISHED) return checked
        return evaluateEnd(advanceTurn(checked))
    }

    /**
     * Moves the turn to the next player following [GameState.direction], skipping
     * players who left and the one marked by a SKIP. Counts a round every time the
     * turn wraps around, and ticks down the active event.
     */
    private fun advanceTurn(state: GameState): GameState {
        val n = state.players.size
        var index = state.currentTurn
        var round = state.round
        var skipped = state.skippedPlayerId

        var steps = 0
        while (steps <= 2 * n) {
            val previous = index
            index = Math.floorMod(index + state.direction, n)
            if ((state.direction == 1 && index <= previous) || (state.direction == -1 && index >= previous)) round++
            steps++

            val candidate = state.players[index]
            if (candidate.abandoned) continue
            if (candidate.id == skipped) {
                skipped = null
                continue
            }
            break
        }

        val gained = round - state.round
        val event = state.activeEvent?.let {
            if (it.roundsLeft > 0) it.copy(roundsLeft = maxOf(0, it.roundsLeft - gained)) else it
        }
        return state.copy(currentTurn = index, round = round, skippedPlayerId = skipped, activeEvent = event)
    }

    /** Id of the next player (in the current direction) who has not left, or null if there is none. */
    private fun nextActivePlayerId(state: GameState, from: Int): String? {
        val n = state.players.size
        for (step in 1 until n) {
            val candidate = state.players[Math.floorMod(from + state.direction * step, n)]
            if (!candidate.abandoned) return candidate.id
        }
        return null
    }

    /** Highest score; tie -> fewer powers used; still tied -> null. Players who left cannot win. */
    private fun decideWinner(players: List<Player>): Player? {
        val candidates = players.filter { !it.abandoned }
        val best = candidates.maxOfOrNull { it.score } ?: return null
        val top = candidates.filter { it.score == best }
        val fewest = top.minOf { it.powersUsed }
        return top.filter { it.powersUsed == fewest }.singleOrNull()
    }

    /** Who starts: whoever holds the highest double, or else the highest tile. */
    private fun startingPlayerIndex(players: List<Player>): Int {
        fun weight(tile: Tile) = if (tile.isDouble()) 100 + tile.points() else tile.points()
        return players.indices.maxBy { i -> players[i].hand.maxOf { weight(it) } }
    }

    private fun fullSet(): List<Tile> =
        (0..MAX_PIP).flatMap { a -> (a..MAX_PIP).map { b -> Tile(a, b) } }

    private fun freshPowerUses(): Map<String, Int> =
        PowerType.entries.associate { it.name to INITIAL_USES_PER_POWER }

    /** Consumes one use of [power] (if any) and counts it for the tie-breaker. */
    private fun Player.spend(power: PowerType?): Player =
        if (power == null) this
        else copy(
            powerUses = powerUses + (power.name to usesLeft(power) - 1),
            powersUsed = powersUsed + 1
        )

    private fun <T> List<T>.replaceAt(index: Int, item: T): List<T> =
        mapIndexed { i, old -> if (i == index) item else old }

    private fun PowerType.label(): String = when (this) {
        PowerType.WILDCARD -> "Wildcard"
        PowerType.SKIP -> "Skip"
        PowerType.SWAP -> "Swap"
        PowerType.REVERSE -> "Reverse"
    }

    // endregion

    companion object {
        const val HAND_SIZE = 7
        const val MIN_PLAYERS = 2
        const val MAX_PLAYERS = 4
        const val MAX_PIP = 6
        /** Chance that a random event triggers each time [applyRandomEvent] is called. */
        const val EVENT_PROBABILITY = 0.25
    }
}