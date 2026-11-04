package com.example.domino.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.domino.domain.model.Board
import com.example.domino.domain.model.Player
import com.example.domino.domain.model.PowerType
import com.example.domino.domain.model.Side
import com.example.domino.domain.model.Tile
import com.example.domino.ui.theme.DominoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * B7: pruebas de UI (instrumentadas) de los componentes reutilizables.
 * Se ejecutan con un emulador o dispositivo: Android Studio > clic derecho > Run.
 */
class ComponentesUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    // ---------- FichaView ----------

    @Test
    fun ficha_habilitada_llama_onClick() {
        var clics = 0
        composeRule.setContent {
            DominoTheme { FichaView(tile = Tile(3, 4), onClick = { clics++ }) }
        }

        composeRule.onNodeWithTag("ficha_3_4").performClick()

        assertEquals(1, clics)
    }

    @Test
    fun ficha_deshabilitada_no_llama_onClick() {
        var clics = 0
        composeRule.setContent {
            DominoTheme { FichaView(tile = Tile(3, 4), habilitada = false, onClick = { clics++ }) }
        }

        composeRule.onNodeWithTag("ficha_3_4").assertIsNotEnabled()
        composeRule.onNodeWithTag("ficha_3_4").performClick()

        assertEquals(0, clics)
    }

    // ---------- JugadorCard ----------

    @Test
    fun jugadorCard_muestra_nombre_puntaje_y_turno() {
        composeRule.setContent {
            DominoTheme {
                JugadorCard(
                    jugador = Player(id = "u1", name = "Ana", score = 980),
                    esTurno = true,
                    esYo = false
                )
            }
        }

        composeRule.onNodeWithText("Ana").assertIsDisplayed()
        composeRule.onNodeWithText("980 pts").assertIsDisplayed()
        composeRule.onNodeWithText("TURNO").assertIsDisplayed()
    }

    @Test
    fun jugadorCard_sin_turno_no_muestra_etiqueta_turno() {
        composeRule.setContent {
            DominoTheme {
                JugadorCard(
                    jugador = Player(id = "u2", name = "Luis", score = 100),
                    esTurno = false,
                    esYo = true
                )
            }
        }

        composeRule.onNodeWithText("Luis (Tú)").assertIsDisplayed()
        composeRule.onNodeWithText("100 pts").assertIsDisplayed() // el puntaje siempre visible
        composeRule.onNodeWithText("TURNO").assertDoesNotExist()
    }

    // ---------- PoderButton ----------

    @Test
    fun poderButton_con_usos_responde_al_clic() {
        var clics = 0
        composeRule.setContent {
            DominoTheme {
                PoderButton(
                    poder = PowerType.WILDCARD,
                    usosRestantes = 2,
                    seleccionado = false,
                    habilitado = true,
                    onClick = { clics++ }
                )
            }
        }

        composeRule.onNodeWithTag("poder_WILDCARD").assertIsEnabled()
        composeRule.onNodeWithTag("poder_WILDCARD").performClick()

        assertEquals(1, clics)
    }

    @Test
    fun poderButton_sin_usos_queda_deshabilitado() {
        var clics = 0
        composeRule.setContent {
            DominoTheme {
                PoderButton(
                    poder = PowerType.SKIP,
                    usosRestantes = 0,
                    seleccionado = false,
                    habilitado = true,
                    onClick = { clics++ }
                )
            }
        }

        composeRule.onNodeWithTag("poder_SKIP").assertIsNotEnabled()
        composeRule.onNodeWithTag("poder_SKIP").performClick()

        assertEquals(0, clics)
    }

    // ---------- ControlesPartida ----------

    @Test
    fun controles_fuera_de_turno_deshabilitan_robar_y_poderes_pero_no_abandonar() {
        var abandonos = 0
        composeRule.setContent {
            DominoTheme {
                ControlesPartida(
                    esMiTurno = false,
                    fichasEnPozo = 5,
                    poderSeleccionado = null,
                    onRobar = {},
                    onPoderes = {},
                    onAbandonar = { abandonos++ }
                )
            }
        }

        composeRule.onNodeWithTag("btn_robar").assertIsNotEnabled()
        composeRule.onNodeWithTag("btn_poderes").assertIsNotEnabled()
        composeRule.onNodeWithTag("btn_abandonar").assertIsEnabled()
        composeRule.onNodeWithTag("btn_abandonar").performClick()

        assertEquals(1, abandonos)
    }

    @Test
    fun controles_con_pozo_vacio_muestran_pasar_turno() {
        composeRule.setContent {
            DominoTheme {
                ControlesPartida(
                    esMiTurno = true,
                    fichasEnPozo = 0,
                    poderSeleccionado = null,
                    onRobar = {},
                    onPoderes = {},
                    onAbandonar = {}
                )
            }
        }

        composeRule.onNodeWithText("Pasar turno").assertIsDisplayed()
    }

    // ---------- TableroView ----------

    @Test
    fun tablero_con_ficha_seleccionada_permite_elegir_extremo() {
        val lados = mutableListOf<Side>()
        composeRule.setContent {
            DominoTheme {
                TableroView(
                    board = Board(listOf(Tile(2, 3), Tile(3, 6))),
                    mostrarExtremos = true,
                    onExtremo = { lados.add(it) }
                )
            }
        }

        composeRule.onNodeWithTag("extremo_izquierdo").performClick()
        composeRule.onNodeWithTag("extremo_derecho").performClick()

        assertEquals(listOf(Side.LEFT, Side.RIGHT), lados)
    }

    @Test
    fun tablero_sin_ficha_seleccionada_deja_los_extremos_inactivos() {
        var clics = 0
        composeRule.setContent {
            DominoTheme {
                TableroView(
                    board = Board(listOf(Tile(2, 3))),
                    mostrarExtremos = false,
                    onExtremo = { clics++ }
                )
            }
        }

        composeRule.onNodeWithTag("extremo_izquierdo").assertIsNotEnabled()
        composeRule.onNodeWithTag("extremo_izquierdo").performClick()

        assertEquals(0, clics)
    }
}
