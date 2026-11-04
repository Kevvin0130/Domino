package com.example.domino.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

/**
 * B2: muestra el selector de cuentas de Google y devuelve el idToken.
 * Necesita un contexto de Activity (LocalContext dentro de Compose lo es).
 */
object GoogleSignInHelper {

    suspend fun obtenerIdToken(contexto: Context): Result<String> {
        // default_web_client_id lo genera el plugin de google-services SOLO si el
        // google-services.json trae el cliente web (aparece al agregar el SHA-1 y
        // habilitar Google en Firebase). Se busca por nombre para no romper la compilación.
        val idRecurso = contexto.resources.getIdentifier(
            "default_web_client_id", "string", contexto.packageName
        )
        if (idRecurso == 0) {
            return Result.failure(
                IllegalStateException("Falta configurar Google en Firebase (SHA-1 y google-services.json).")
            )
        }

        return try {
            val opcion = GetSignInWithGoogleOption.Builder(contexto.getString(idRecurso)).build()
            val solicitud = GetCredentialRequest.Builder().addCredentialOption(opcion).build()
            val respuesta = CredentialManager.create(contexto).getCredential(contexto, solicitud)
            val credencial = respuesta.credential

            if (credencial is CustomCredential &&
                credencial.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                Result.success(GoogleIdTokenCredential.createFrom(credencial.data).idToken)
            } else {
                Result.failure(IllegalStateException("Tipo de credencial no soportado."))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(IllegalStateException("Inicio de sesión con Google cancelado."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
