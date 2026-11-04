package com.example.domino.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domino.domain.model.Board
import com.example.domino.domain.model.Player
import com.example.domino.domain.model.PowerType
import com.example.domino.domain.model.Tile
import com.example.domino.ui.components.ControlesPartida
import com.example.domino.ui.components.FichaView
import com.example.domino.ui.components.JugadorCard
import com.example.domino.ui.components.PoderButton
import com.example.domino.ui.components.TableroView
import com.example.domino.ui.theme.DominoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** B7: pruebas de UI de los componentes reutilizables (Compose Test, sin Firebase ni motor). */
@RunWith(AndroidJUnit4::class)
class ComponentesUiTest {

    @get:Rule
    val rule = createComposeRule()

    private val ana = Player(id = "u2", name = "Ana", score = 980, hand = List(7) { Tile(it, 1) })

    // ---------- FichaView ----------

    @Test
    fun fichaView_alTocarla_invocaElCallback() {
        var toques = 0
        rule.setContent { DominoTheme { FichaView(ficha = Tile(3, 5), onClick = { toques++ }) } }

        rule.onNodeWithTag("ficha_3_5").performClick()

        assertEquals(1, toques)
    }

    @Test
    fun fichaView_oculta_noRevelaSusValores() {
        rule.setContent { DominoTheme { FichaView(ficha = Tile(3, 5), oculta = true) } }

        rule.onNodeWithTag("ficha_oculta").assertIsDisplayed()
        rule.onNodeWithTag("ficha_3_5").assertDoesNotExist()
    }

    // ---------- JugadorCard ----------

    @Test
    fun jugadorCard_enTurno_muestraTurnoYPuntaje() {
        rule.setContent { DominoTheme { JugadorCard(jugador = ana, esTurno = true) } }

        rule.onNodeWithText("● TURNO").assertIsDisplayed()
        rule.onNodeWithText("Ana").assertIsDisplayed()
        rule.onNodeWithText("980 pts").assertIsDisplayed()
        rule.onNodeWithText("7 fichas").assertIsDisplayed()
    }

    @Test
    fun jugadorCard_sinTurno_noMuestraElIndicador_peroSiElPuntaje() {
        rule.setContent { DominoTheme { JugadorCard(jugador = ana, esTurno = false) } }

        rule.onNodeWithText("● TURNO").assertDoesNotExist()
        rule.onNodeWithText("980 pts").assertIsDisplayed()
    }

    @Test
    fun jugadorCard_esYo_agregaTu() {
        rule.setContent { DominoTheme { JugadorCard(jugador = ana, esTurno = false, esYo = true) } }

        rule.onNodeWithText("Ana (tú)").assertIsDisplayed()
    }

    // ---------- PoderButton ----------

    @Test
    fun poderButton_conUsos_muestraNombreYUsos_yResponde() {
        var toques = 0
        rule.setContent {
            DominoTheme {
                PoderButton(poder = PowerType.WILDCARD, usosRestantes = 2, onClick = { toques++ })
            }
        }

        rule.onNodeWithText("Comodín").assertIsDisplayed()
        rule.onNodeWithText("x2").assertIsDisplayed()
        rule.onNodeWithTag("poder_WILDCARD").performClick()
        assertEquals(1, toques)
    }

    @Test
    fun poderButton_sinUsos_estaDeshabilitado() {
        var toques = 0
        rule.setContent {
            DominoTheme {
                PoderButton(poder = PowerType.SKIP, usosRestantes = 0, onClick = { toques++ })
            }
        }

        rule.onNodeWithTag("poder_SKIP").assertIsNotEnabled()
        rule.onNodeWithTag("poder_SKIP").performClick()
        assertEquals(0, toques)
    }

    // ---------- ControlesPartida ----------

    @Test
    fun controles_fueraDeTurno_soloPermitenAbandonar() {
        rule.setContent {
            DominoTheme {
                ControlesPartida(
                    tableroVacio = false,
                    puedeColocarIzquierda = false,
                    puedeColocarDerecha = false,
                    puedeRobar = false,
                    textoRobar = "Robar (4)",
                    puedePoderes = false,
                    poderActivo = null,
                    onColocarIzquierda = {},
                    onColocarDerecha = {},
                    onRobar = {},
                    onPoderes = {},
                    onAbandonar = {}
                )
            }
        }

        rule.onNodeWithTag("btn_izquierda").assertIsNotEnabled()
        rule.onNodeWithTag("btn_derecha").assertIsNotEnabled()
        rule.onNodeWithTag("btn_robar").assertIsNotEnabled()
        rule.onNodeWithTag("btn_poderes").assertIsNotEnabled()
        rule.onNodeWithTag("btn_abandonar").assertIsEnabled()
    }

    @Test
    fun controles_conJugadaValida_colocaPorElLadoElegido() {
        var lado = ""
        rule.setContent {
            DominoTheme {
                ControlesPartida(
                    tableroVacio = false,
                    puedeColocarIzquierda = true,
                    puedeColocarDerecha = true,
                    puedeRobar = false,
                    textoRobar = "Robar (4)",
                    puedePoderes = true,
                    poderActivo = null,
                    onColocarIzquierda = { lado = "izq" },
                    onColocarDerecha = { lado = "der" },
                    onRobar = {},
                    onPoderes = {},
                    onAbandonar = {}
                )
            }
        }

        rule.onNodeWithTag("btn_izquierda").performClick()
        assertEquals("izq", lado)
        rule.onNodeWithTag("btn_derecha").performClick()
        assertEquals("der", lado)
    }

    @Test
    fun controles_tableroVacio_ofreceUnSoloBotonDeColocar() {
        rule.setContent {
            DominoTheme {
                ControlesPartida(
                    tableroVacio = true,
                    puedeColocarIzquierda = false,
                    puedeColocarDerecha = true,
                    puedeRobar = false,
                    textoRobar = "Pasar turno",
                    puedePoderes = true,
                    poderActivo = PowerType.REVERSE,
                    onColocarIzquierda = {},
                    onColocarDerecha = {},
                    onRobar = {},
                    onPoderes = {},
                    onAbandonar = {}
                )
            }
        }

        rule.onNodeWithTag("btn_izquierda").assertDoesNotExist()
        rule.onNodeWithText("Colocar ficha").assertIsEnabled()
        rule.onNodeWithText("Poder: Inversión").assertIsDisplayed()
    }

    // ---------- TableroView ----------

    @Test
    fun tablero_vacio_muestraElMensaje() {
        rule.setContent { DominoTheme { TableroView(tablero = Board()) } }

        rule.onNodeWithText("Tablero vacío: coloca la primera ficha").assertIsDisplayed()
    }

    @Test
    fun tablero_conFichas_muestraLosExtremosAbiertos() {
        rule.setContent {
            DominoTheme { TableroView(tablero = Board(listOf(Tile(2, 3), Tile(3, 6)))) }
        }

        rule.onNodeWithText("Izquierda: 2").assertIsDisplayed()
        rule.onNodeWithText("Derecha: 6").assertIsDisplayed()
    }
}
