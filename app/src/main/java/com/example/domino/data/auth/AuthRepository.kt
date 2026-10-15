
package com.example.domino.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    fun registrar(
        nombreUsuario: String,
        correo: String,
        contrasena: String,
        onResultado: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(
            correo,
            contrasena
        ).addOnCompleteListener { resultadoregistro ->

            if (resultadoregistro.isSuccessful) {

                val usuario = auth.currentUser //auth.currentUser representa al usuario que está autenticado actualmente en Firebase.

                if (usuario == null) {
                    onResultado(
                        false,
                        "La cuenta se creó, pero no se pudo obtener el usuario."
                    )
                    return@addOnCompleteListener //Termina la ejecucion del lambda que habiamos creado
                }

                val perfil = UserProfileChangeRequest.Builder() //Se prepara una solicitud para establecer el nombre visible del usuario autenticado
                    .setDisplayName(nombreUsuario)
                    .build()

                usuario.updateProfile(perfil) //Guardar el nombre en el perfil
                    .addOnCompleteListener { tareaPerfil ->

                        if (tareaPerfil.isSuccessful) {
                            onResultado(true, null)
                        } else {
                            onResultado(
                                true,
                                "La cuenta se creó, pero no se pudo guardar el nombre."
                            )
                        }
                    }

            } else {
                onResultado(
                    false,
                    resultadoregistro.exception?.localizedMessage //Si Firebase proporciona un mensaje lo usmos; si no, muestra un mensaje genérico
                        ?: "No se pudo crear la cuenta."
                )
            }
        }
    }

    fun recuperarContrasena(
        correo: String,
        onResultado: (Boolean, String?) -> Unit
    ) {
        auth.sendPasswordResetEmail(correo.trim())
            .addOnCompleteListener { tarea ->

                if (tarea.isSuccessful) {
                    onResultado(
                        true,
                        "Si el correo es válido, revisa tu bandeja de entrada."
                    )
                } else {
                    onResultado(
                        false,
                        tarea.exception?.localizedMessage
                            ?: "No se pudo procesar la solicitud."
                    )
                }
            }
    }

    fun iniciarSesion(
        correo: String,
        contrasena: String,
        onResultado: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(
            correo.trim(),
            contrasena
        ).addOnCompleteListener { tarea ->

            if (tarea.isSuccessful) {
                onResultado(true, null)
            } else {
                onResultado(
                    false,
                    "No se pudo iniciar sesión. Revisa tu correo y contraseña."
                )
            }
        }
    }


}
