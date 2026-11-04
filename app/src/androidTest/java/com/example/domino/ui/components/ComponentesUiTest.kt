package com.example.domino.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.domino.domain.model.Player
import com.example.domino.domain.model.Tile
import org.junit.Rule
import org.junit.Test

class ComponentUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dominoButton_displaysText_andTriggersClick() {
        var clicked = false

        composeTestRule.setContent {
            DominoButton(
                text = "Start Game",
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Start Game").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Game").performClick()

        assert(clicked)
    }

    @Test
    fun tileView_displaysTileValues() {
        val testTile = Tile(left = 6, right = 6)

        composeTestRule.setContent {
            TileView(tile = testTile)
        }

        // Se utiliza onAllNodesWithText[0] porque la ficha doble (6-6) tiene dos numeros 6
        composeTestRule.onAllNodesWithText("6")[0].assertIsDisplayed()
    }

    @Test
    fun playerCard_displaysPlayerInfo() {
        val testPlayer = Player(id = "1", name = "John Doe", score = 150)

        composeTestRule.setContent {
            PlayerCard(player = testPlayer, isCurrentTurn = true)
        }

        // Verifica que se muestre el nombre del jugador
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()

        // Verifica que el número de puntaje aparezca en pantalla
        composeTestRule.onAllNodesWithText("150", substring = true)[0].assertIsDisplayed()
    }
}
