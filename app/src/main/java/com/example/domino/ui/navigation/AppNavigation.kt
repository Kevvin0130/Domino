package com.example.domino.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.domino.domain.model.GamePhase
import com.example.domino.ui.components.MensajeEffect
import com.example.domino.ui.screens.BienvenidaScreen
import com.example.domino.ui.screens.ComoJugarScreen
import com.example.domino.ui.screens.CrearPartidaScreen
import com.example.domino.ui.screens.HistorialScreen
import com.example.domino.ui.screens.IniciarSesionScreen
import com.example.domino.ui.screens.MenuPrincipalScreen
import com.example.domino.ui.screens.RecuperarContrasenaScreen
import com.example.domino.ui.screens.RegistroScreen
import com.example.domino.ui.screens.ResultadoScreen
import com.example.domino.ui.screens.SalaEsperaScreen
import com.example.domino.ui.screens.TableroScreen
import com.example.domino.ui.screens.UnirPartidaScreen
import com.example.domino.ui.viewmodel.AuthViewModel
import com.example.domino.ui.viewmodel.GameViewModel
import com.example.domino.ui.viewmodel.HistorialViewModel
import com.example.domino.ui.viewmodel.LobbyViewModel

private object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val INICIAR_SESION = "iniciar_sesion"
    const val REGISTRO = "registro"
    const val RECUPERAR_CONTRASENA = "recuperar_contrasena"
    const val MENU = "menu"
    const val CREAR = "crear"
    const val UNIRSE = "unirse"
    const val COMOJUGAR = "comojugar"
    const val HISTORIAL = "historial"

    // Rutas con argumento: el código de la sala.
    const val ARG_CODIGO = "codigo"
    const val SALA_PATRON = "sala/{codigo}"
    const val JUEGO_PATRON = "juego/{codigo}"
    const val RESULTADO_PATRON = "resultado/{codigo}"

    fun sala(codigo: String) = "sala/$codigo"
    fun juego(codigo: String) = "juego/$codigo"
    fun resultado(codigo: String) = "resultado/$codigo"
}

private val argumentoCodigo = listOf(navArgument(Rutas.ARG_CODIGO) { type = NavType.StringType })

/** Borra toda la pila y deja [ruta] como única pantalla. */
private fun NavHostController.irLimpiando(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.id) { inclusive = true }
    }
}

/**
 * B1 + B5: solo navega. La lógica de auth vive en AuthViewModel, la del lobby en LobbyViewModel
 * y la de la partida en GameViewModel; aquí únicamente se conectan con las pantallas.
 *
 * Los ViewModels se crean aquí (alcance de la Activity) para que Sala de espera, Tablero y
 * Resultado compartan el MISMO GameViewModel y por tanto la misma conexión a Firestore.
 */
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
    lobbyViewModel: LobbyViewModel = viewModel(),
    gameViewModel: GameViewModel = viewModel(),
    historialViewModel: HistorialViewModel = viewModel()
) {
    val contexto = LocalContext.current
    val navController = rememberNavController()

    val auth by authViewModel.uiState.collectAsState()
    val lobby by lobbyViewModel.uiState.collectAsState()
    val partida by gameViewModel.uiState.collectAsState()
    val historial by historialViewModel.uiState.collectAsState()

    // Si ya hay sesión guardada se abre directo el menú.
    val inicio = remember { if (authViewModel.haySesion()) Rutas.MENU else Rutas.BIENVENIDA }

    // --- Mensajes de los ViewModels (Toast una sola vez) ---
    MensajeEffect(auth.mensaje) { authViewModel.mensajeMostrado() }
    MensajeEffect(lobby.error) { lobbyViewModel.clearError() }
    MensajeEffect(partida.error) { gameViewModel.clearError() }

    // --- Auth exitoso -> menú (sin dejar pantallas de login en la pila) ---
    LaunchedEffect(auth.autenticado) {
        if (auth.autenticado) {
            navController.irLimpiando(Rutas.MENU)
            authViewModel.navegacionManejada()
        }
    }

    // --- Sala creada / unida -> sala de espera ---
    LaunchedEffect(lobby.roomCode) {
        val codigo = lobby.roomCode ?: return@LaunchedEffect
        navController.navigate(Rutas.sala(codigo)) {
            popUpTo(Rutas.MENU) // al volver atrás se regresa al menú, no a Crear/Unirse
        }
        lobbyViewModel.roomHandled()
    }

    NavHost(
        navController = navController,
        startDestination = inicio
    ) {
        // ---------- Auth ----------
        composable(Rutas.BIENVENIDA) {
            BienvenidaScreen(
                onIniciarSesion = { navController.navigate(Rutas.INICIAR_SESION) },
                onCrearCuenta = { navController.navigate(Rutas.REGISTRO) },
                onGoogle = {
                    // B2 (opcional): ver carpeta OPCIONAL_B2_Google.
                    Toast.makeText(contexto, "Ingreso con Google no disponible todavía.", Toast.LENGTH_SHORT).show()
                }
            )
        }
        composable(Rutas.INICIAR_SESION) {
            IniciarSesionScreen(
                onIniciarSesion = { correo, contrasena -> authViewModel.iniciarSesion(correo, contrasena) },
                onIrRegistro = { navController.navigate(Rutas.REGISTRO) },
                onVolver = { navController.popBackStack() },
                onRecuperarContrasena = { navController.navigate(Rutas.RECUPERAR_CONTRASENA) }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistrar = { nombre, correo, contrasena -> authViewModel.registrar(nombre, correo, contrasena) },
                onIrIniciarSesion = { navController.navigate(Rutas.INICIAR_SESION) },
                onVolver = { navController.popBackStack() }
            )
        }
        composable(Rutas.RECUPERAR_CONTRASENA) {
            RecuperarContrasenaScreen(
                onRecuperar = { correo -> authViewModel.recuperarContrasena(correo) },
                onVolver = { navController.popBackStack() }
            )
        }

        // ---------- Menú y lobby ----------
        composable(Rutas.MENU) {
            MenuPrincipalScreen(
                onJugar = { navController.navigate(Rutas.CREAR) },
                onCrearSala = { navController.navigate(Rutas.CREAR) },
                onUnirseSala = { navController.navigate(Rutas.UNIRSE) },
                onComoJugar = { navController.navigate(Rutas.COMOJUGAR) },
                onHistorial = { navController.navigate(Rutas.HISTORIAL) },
                onCerrarSesion = {
                    authViewModel.cerrarSesion()
                    navController.irLimpiando(Rutas.BIENVENIDA)
                }
            )
        }
        composable(Rutas.COMOJUGAR) {
            ComoJugarScreen(onVolver = { navController.popBackStack() })
        }
        composable(Rutas.CREAR) {
            CrearPartidaScreen(
                onVolver = { navController.popBackStack() },
                onCrearPartida = { lobbyViewModel.createRoom() },
                cargando = lobby.loading
            )
        }
        composable(Rutas.UNIRSE) {
            UnirPartidaScreen(
                onVolver = { navController.popBackStack() },
                onUnirse = { codigo -> lobbyViewModel.join(codigo) },
                cargando = lobby.loading
            )
        }
        composable(Rutas.HISTORIAL) {
            LaunchedEffect(Unit) { historialViewModel.cargar(authViewModel.usuarioId()) }
            HistorialScreen(
                partidas = historial.partidas,
                cargando = historial.cargando,
                onVolver = { navController.popBackStack() }
            )
        }

        // ---------- Partida ----------
        composable(route = Rutas.SALA_PATRON, arguments = argumentoCodigo) { entrada ->
            val codigo = entrada.arguments?.getString(Rutas.ARG_CODIGO).orEmpty()
            val miId = authViewModel.usuarioId()
            LaunchedEffect(codigo, miId) { gameViewModel.connect(codigo, miId) }

            // Ignora el estado de una sala anterior mientras llega el de esta.
            val estado = partida.state?.takeIf { it.code == codigo }

            LaunchedEffect(estado?.phase) {
                if (estado?.phase == GamePhase.IN_PROGRESS) {
                    navController.navigate(Rutas.juego(codigo)) {
                        popUpTo(Rutas.SALA_PATRON) { inclusive = true }
                    }
                }
            }
            SalaEsperaScreen(
                codigo = codigo,
                estado = estado,
                miId = miId,
                onIniciar = { gameViewModel.startGame() },
                onSalir = {
                    gameViewModel.abandon()
                    navController.popBackStack(Rutas.MENU, false)
                }
            )
        }

        composable(route = Rutas.JUEGO_PATRON, arguments = argumentoCodigo) { entrada ->
            val codigo = entrada.arguments?.getString(Rutas.ARG_CODIGO).orEmpty()
            val miId = authViewModel.usuarioId()
            LaunchedEffect(codigo, miId) { gameViewModel.connect(codigo, miId) }

            val estado = partida.state?.takeIf { it.code == codigo }

            LaunchedEffect(estado?.phase) {
                if (estado?.phase == GamePhase.FINISHED) {
                    navController.navigate(Rutas.resultado(codigo)) {
                        popUpTo(Rutas.JUEGO_PATRON) { inclusive = true }
                    }
                }
            }
            TableroScreen(
                ui = if (estado == null) partida.copy(state = null) else partida,
                onSeleccionarFicha = { gameViewModel.selectTile(it) },
                onSeleccionarPoder = { gameViewModel.selectPower(it) },
                onSeleccionarObjetivo = { id, ficha -> gameViewModel.selectSwapTarget(id, ficha) },
                onConfirmarIntercambio = { gameViewModel.confirmSwap() },
                onColocar = { lado -> gameViewModel.placeTile(lado) },
                onRobar = { gameViewModel.drawFromPile() },
                onAbandonar = {
                    gameViewModel.abandon()
                    navController.popBackStack(Rutas.MENU, false)
                },
                onSalir = { navController.popBackStack(Rutas.MENU, false) }
            )
        }

        composable(route = Rutas.RESULTADO_PATRON, arguments = argumentoCodigo) { entrada ->
            val codigo = entrada.arguments?.getString(Rutas.ARG_CODIGO).orEmpty()
            val miId = authViewModel.usuarioId()
            LaunchedEffect(codigo, miId) { gameViewModel.connect(codigo, miId) }

            val estado = partida.state?.takeIf { it.code == codigo }

            // Si el anfitrión reinicia, todos vuelven a la sala o directo al juego.
            LaunchedEffect(estado?.phase) {
                when (estado?.phase) {
                    GamePhase.IN_PROGRESS -> navController.navigate(Rutas.juego(codigo)) {
                        popUpTo(Rutas.RESULTADO_PATRON) { inclusive = true }
                    }
                    GamePhase.WAITING -> navController.navigate(Rutas.sala(codigo)) {
                        popUpTo(Rutas.RESULTADO_PATRON) { inclusive = true }
                    }
                    else -> Unit
                }
            }
            ResultadoScreen(
                estado = estado,
                miId = miId,
                onJugarDeNuevo = { gameViewModel.restart() },
                onSalir = {
                    gameViewModel.abandon()
                    navController.popBackStack(Rutas.MENU, false)
                }
            )
        }
    }
}
