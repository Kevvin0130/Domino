
package com.example.domino

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.domino.ui.navigation.AppNavigation
import com.example.domino.ui.theme.DominoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DominoTheme {
                AppNavigation()
            }
        }
    }
}