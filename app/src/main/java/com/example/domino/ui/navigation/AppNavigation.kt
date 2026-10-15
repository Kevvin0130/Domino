
package com.example.domino.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.domino.data.auth.AuthRepository
import com.example.domino.ui.screens.BienvenidaScreen
import com.example.domino.ui.screens.ComoJugarScreen
import com.example.domino.ui.screens.CrearPartidaScreen
import com.example.domino.ui.screens.IniciarSesionScreen
import com.example.domino.ui.screens.RegistroScreen
import com.example.domino.ui.screens.MenuPrincipalScreen
import com.example.domino.ui.screens.RecuperarContrasenaScreen
import com.example.domino.ui.screens.UnirPartidaScreen
import android.widget.Toast

private object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val INICIAR_SESION = "iniciar_sesion"
    const val REGISTRO = "registro"
    const val RECUPERAR_CONTRASENA = "recuperar_contrasena"
    const val MENU = "menu"
    const val CREAR = "crear"
    const val UNIRSE = "unirse"
    const val COMOJUGAR = "comojugar"
}

@Composable
fun AppNavigation() {
    val contexto = LocalContext.current
    val authRepository = remember { AuthRepository() } //Se cre esto, porque queremos reutilizar el repositorio desde la navegación, sin estar creando continuamente instancias nuevas cuando Compose vuelve a dibujar partes de la interfaz.
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.BIENVENIDA
    ) {
        composable(Rutas.BIENVENIDA) {
            BienvenidaScreen(
                onIniciarSesion = {
                    navController.navigate(Rutas.INICIAR_SESION)
                },
                onCrearCuenta = {
                    navController.navigate(Rutas.REGISTRO)
                },
                onGoogle = {
                    // Próximamente conectaremos Firebase y Google.
                }
            )
        }
        composable(Rutas.INICIAR_SESION) {
            IniciarSesionScreen(
                onIniciarSesion = { correo, contrasena ->
                    authRepository.iniciarSesion(
                        correo = correo,
                        contrasena = contrasena
                    ) { exitoso, mensaje ->
                        if (exitoso) {
                            navController.navigate(Rutas.MENU) {
                                popUpTo(Rutas.BIENVENIDA) {
                                    inclusive = true
                                }
                            }
                        } else {
                            Toast.makeText(
                                contexto,
                                mensaje
                                    ?: "No se pudo iniciar sesión.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                onIrRegistro = {
                    navController.navigate(Rutas.REGISTRO)
                },
                onVolver = {
                    navController.popBackStack()
                },
                onRecuperarContrasena = {
                    navController.navigate(route = Rutas.RECUPERAR_CONTRASENA)
                }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistrar = { nombre, correo, contrasena ->
                    authRepository.registrar(
                        nombreUsuario = nombre,
                        correo = correo,
                        contrasena = contrasena
                    ){ exitoso, mensaje ->

                        if (exitoso) {

                            if (mensaje != null) {
                                Toast.makeText(
                                    contexto,
                                    mensaje,
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            navController.navigate(Rutas.MENU) {
                                popUpTo(Rutas.BIENVENIDA) {
                                    inclusive = true
                                }
                            }//En esta parte del codigo lo que se hace es eliminar las pantallas anteriores a la de menu, ya que
                            //si el registro fue exitoso nos mandara a la pantalla de menu, y lo que hace android es crear un
                            //back stack, que es recopilar como en un pila de cartas las pantallas que se han visitado ya, y se van
                            //agregando una encima de otra
                            //popUpTo significa que queremos retirar pantallas de la pila hasta llegar a un destino determinado.
                            //En nuestro caso, el destino es Rutas.BIENVENIDA.
                            //Por sí solo, popUpTo(Rutas.BIENVENIDA) elimina las pantallas que están por encima de Bienvenida, pero conserva Bienvenida.
                            //Con inclusive=true, elimina esa pantalla seleccionada es decir en nuestro caso Bienvenida, si fuera false la conservaria
                        } else {
                            Toast.makeText(
                                contexto,
                                mensaje ?: "Error al registrar la cuenta",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                onIrIniciarSesion = {
                    navController.navigate(route=Rutas.INICIAR_SESION)
                },
                onVolver = {
                    navController.popBackStack() //Regresa a la pantalla anterior de la pila de navegacion
                }
            )
        }
        composable(route = Rutas.RECUPERAR_CONTRASENA){
            RecuperarContrasenaScreen(
                onRecuperar = { correo ->
                    authRepository.recuperarContrasena(correo) {
                        exitoso, mensaje ->
                            Toast.makeText(
                                contexto,
                                mensaje ?: "Solicitud procesada.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
        composable(Rutas.MENU) {
            MenuPrincipalScreen(
                onJugar = {
                    // Conectaremos la pantalla de partida después.
                },
                onCrearSala = {
                    navController.navigate(route=Rutas.CREAR)
                },
                onUnirseSala = {
                    navController.navigate(route=Rutas.UNIRSE)
                },
                onComoJugar = {
                    navController.navigate(route=Rutas.COMOJUGAR)
                }
            )
        }
        composable(route=Rutas.COMOJUGAR) {
            ComoJugarScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
        composable(route=Rutas.CREAR){
            CrearPartidaScreen(
                onVolver = {
                    navController.popBackStack()
                },
                onCrearPartida = {

                }
            )
        }
        composable(route=Rutas.UNIRSE){
            UnirPartidaScreen(
                onVolver = {
                    navController.popBackStack()
                },
                onUnirse = {

                }
            )
        }
    }
}