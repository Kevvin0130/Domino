package com.example.domino.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
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
        val testTile = Tile(6, 6)

        composeTestRule.setContent {
            TileView(tile = testTile)
        }

        composeTestRule.onNodeWithText("6").assertIsDisplayed()
    }

    @Test
    fun playerCard_displaysPlayerInfo() {
        val testPlayer = Player(id = "1", name = "John Doe", score = 150)

        composeTestRule.setContent {
            PlayerCard(player = testPlayer, isCurrentTurn = true)
        }

        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Score: 150").assertIsDisplayed()
    }
}
