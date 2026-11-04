package com.example.domino.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.domino.ui.screens.BienvenidaScreen
import com.example.domino.ui.screens.ComoJugarScreen
import com.example.domino.ui.screens.CrearPartidaScreen
import com.example.domino.ui.screens.HistorialScreen
import com.example.domino.ui.screens.IniciarSesionScreen
import com.example.domino.ui.screens.MenuPrincipalScreen
import com.example.domino.ui.screens.PartidaScreen
import com.example.domino.ui.screens.RecuperarContrasenaScreen
import com.example.domino.ui.screens.RegistroScreen
import com.example.domino.ui.screens.UnirPartidaScreen
import com.example.domino.ui.viewmodel.AuthViewModel
import com.example.domino.ui.viewmodel.GameViewModel
import com.example.domino.ui.viewmodel.HistorialViewModel
import com.example.domino.ui.viewmodel.LobbyViewModel

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object MainMenu : Screen("main_menu")
    object CreateGame : Screen("create_game")
    object JoinGame : Screen("join_game")
    object HowToPlay : Screen("how_to_play")
    object History : Screen("history")
    object Game : Screen("game/{code}") {
        fun createRoute(code: String) = "game/$code"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val lobbyViewModel: LobbyViewModel = viewModel()
    val context = LocalContext.current

    val authState by authViewModel.uiState.collectAsState()
    val lobbyState by lobbyViewModel.uiState.collectAsState()

    LaunchedEffect(authState.mensaje) {
        authState.mensaje?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            authViewModel.mensajeMostrado()
        }
    }

    LaunchedEffect(authState.autenticado) {
        if (authState.autenticado) {
            authViewModel.navegacionManejada()
            navController.navigate(Screen.MainMenu.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val startDestination = if (authViewModel.haySesion()) Screen.MainMenu.route else Screen.Welcome.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Welcome.route) {
            BienvenidaScreen(
                onIniciarSesion = { navController.navigate(Screen.Login.route) },
                onCrearCuenta = { navController.navigate(Screen.Register.route) },
                onGoogle = {}
            )
        }

        composable(Screen.Login.route) {
            IniciarSesionScreen(
                onIniciarSesion = { correo, pass -> authViewModel.iniciarSesion(correo, pass) },
                onRecuperarContrasena = { navController.navigate(Screen.ForgotPassword.route) },
                onIrRegistro = { navController.navigate(Screen.Register.route) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.Register.route) {
            RegistroScreen(
                onRegistrar = { nombre, correo, pass -> authViewModel.registrar(nombre, correo, pass) },
                onIrIniciarSesion = { navController.navigate(Screen.Login.route) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.ForgotPassword.route) {
            RecuperarContrasenaScreen(
                onRecuperar = { correo -> authViewModel.recuperarContrasena(correo) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.MainMenu.route) {
            MenuPrincipalScreen(
                onJugar = { navController.navigate(Screen.CreateGame.route) },
                onCrearSala = {
                    lobbyViewModel.resetState()
                    navController.navigate(Screen.CreateGame.route)
                },
                onUnirseSala = {
                    lobbyViewModel.resetState()
                    navController.navigate(Screen.JoinGame.route)
                },
                onComoJugar = { navController.navigate(Screen.HowToPlay.route) },
                onHistorial = { navController.navigate(Screen.History.route) },
                onCerrarSesion = {
                    authViewModel.cerrarSesion()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CreateGame.route) {
            CrearPartidaScreen(
                onVolver = { navController.popBackStack() },
                onCrearPartida = {
                    lobbyViewModel.createRoom { code ->
                        navController.navigate(Screen.Game.createRoute(code)) {
                            popUpTo(Screen.MainMenu.route)
                        }
                    }
                },
                cargando = lobbyState.isLoading
            )
        }

        composable(Screen.JoinGame.route) {
            UnirPartidaScreen(
                onVolver = { navController.popBackStack() },
                onUnirse = { code ->
                    lobbyViewModel.joinRoom(code) { ok ->
                        if (ok) {
                            navController.navigate(Screen.Game.createRoute(code)) {
                                popUpTo(Screen.MainMenu.route)
                            }
                        }
                    }
                },
                cargando = lobbyState.isLoading
            )
        }

        composable(Screen.HowToPlay.route) {
            ComoJugarScreen(onVolver = { navController.popBackStack() })
        }

        composable(Screen.History.route) {
            val historialViewModel: HistorialViewModel = viewModel()
            val historialState by historialViewModel.uiState.collectAsState()
            val uid = authViewModel.usuarioId()

            LaunchedEffect(uid) {
                historialViewModel.cargar(uid)
            }

            HistorialScreen(
                partidas = historialState.partidas,
                cargando = historialState.cargando,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("code") { type = NavType.StringType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("code").orEmpty()
            val gameViewModel: GameViewModel = viewModel()
            val gameUiState by gameViewModel.uiState.collectAsState()
            val myId = authViewModel.usuarioId()

            LaunchedEffect(code, myId) {
                gameViewModel.connect(code, myId)
            }

            PartidaScreen(
                ui = gameUiState,
                onIniciar = { gameViewModel.startGame() },
                onSeleccionarFicha = { gameViewModel.selectTile(it) },
                onSeleccionarPoder = { gameViewModel.selectPower(it) },
                onSeleccionarObjetivo = { id, tile -> gameViewModel.selectSwapTarget(id, tile) },
                onConfirmarIntercambio = { gameViewModel.confirmSwap() },
                onColocar = { gameViewModel.placeTile(it) },
                onRobar = { gameViewModel.drawFromPile() },
                onAbandonar = { gameViewModel.abandon() },
                onReiniciar = { gameViewModel.restart() },
                onSalir = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
