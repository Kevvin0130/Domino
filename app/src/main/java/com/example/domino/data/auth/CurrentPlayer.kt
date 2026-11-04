package com.example.domino.data.auth

import com.example.domino.domain.model.Player
import com.google.firebase.auth.FirebaseAuth

/**
 * The signed-in user as a game [Player], or null if nobody is signed in.
 * The name is the display name saved at registration (falls back to the email prefix).
 */
fun currentPlayerOrNull(): Player? {
    val user = FirebaseAuth.getInstance().currentUser ?: return null
    val name = user.displayName?.takeIf { it.isNotBlank() }
        ?: user.email?.substringBefore('@')
        ?: "Player"
    return Player(id = user.uid, name = name)
}