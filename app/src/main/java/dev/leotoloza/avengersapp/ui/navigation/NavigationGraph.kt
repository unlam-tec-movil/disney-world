package dev.leotoloza.avengersapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import dev.leotoloza.avengersapp.R
import dev.leotoloza.avengersapp.domain.model.Character
import dev.leotoloza.avengersapp.ui.screens.SplashScreen
import dev.leotoloza.avengersapp.ui.screens.characters.CharacterDetailScreen
import dev.leotoloza.avengersapp.ui.screens.characters.CharactersScreen
import dev.leotoloza.avengersapp.ui.screens.controlPanel.PanelControlScreen
import dev.leotoloza.avengersapp.ui.screens.favorites.FavoritesScreen
import dev.leotoloza.avengersapp.ui.viewmodels.CharactersViewModel
import dev.leotoloza.avengersapp.ui.viewmodels.FavoritesViewModel
import dev.leotoloza.avengersapp.ui.viewmodels.PanelControlViewModel

internal const val MAIN_APP_GRAPH_ROUTE = "main_app_graph"
internal const val CHARACTERS_GRAPH_ROUTE = "characters_graph"

@Composable
fun NavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    onTitleChange: (String) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route,
    ) {
        composable(Screens.Splash.route) {
            SplashScreen(onNavigateToNextScreen = {
                navController.navigate(MAIN_APP_GRAPH_ROUTE) {
                    popUpTo(Screens.Splash.route) {
                        inclusive = true
                    }
                }
            }, onNavigateToAuth = {
                navController.navigate(Screens.Authentication.route) {
                    popUpTo(Screens.Splash.route) {
                        inclusive = true
                    }
                }
            })
        }

        composable(Screens.Authentication.route) {
            val authViewModel: dev.leotoloza.avengersapp.ui.viewmodels.AuthViewModel = hiltViewModel()
            val uiState by authViewModel.uiState.collectAsState()

            // Usamos rememberSaveable para que si el proceso muere durante el login de Google,
            // el estado de "autenticando" sobreviva.
            var isAuthenticating by rememberSaveable { mutableStateOf(false) }
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

            // Función auxiliar para navegar y limpiar el stack
            val onLoginSuccess = {
                // Verificamos si ya estamos en el destino para evitar doble navegación
                if (navController.currentDestination?.route != MAIN_APP_GRAPH_ROUTE) {
                    navController.navigate(MAIN_APP_GRAPH_ROUTE) {
                        popUpTo(Screens.Authentication.route) {
                            inclusive = true
                        }
                    }
                }
            }

            // 1. AuthStateListener: Red de seguridad principal
            androidx.compose.runtime.DisposableEffect(Unit) {
                val listener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { firebaseAuth ->
                    if (firebaseAuth.currentUser != null) {
                        onLoginSuccess()
                    }
                }
                auth.addAuthStateListener(listener)
                onDispose {
                    auth.removeAuthStateListener(listener)
                }
            }

            // 2. Launcher: Maneja el resultado explicito de la actividad
            val firebaseUiLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = com.firebase.ui.auth.FirebaseAuthUIActivityResultContract()
            ) { result ->
                // IMPORTANTE: Bajamos la bandera de carga aca
                isAuthenticating = false

                if (result.resultCode == android.app.Activity.RESULT_OK) {
                    // Forzamos la navegación aca tambien por si el Listener tarda en reaccionar
                    onLoginSuccess()
                } else {
                    val response = result.idpResponse
                    val error = response?.error?.errorCode
                    android.util.Log.e("Auth", "Login cancelado o fallido. Error: $error")
                }
            }

            // 3. Trigger ÚNICO: Solo lanzamos el intent desde un LaunchedEffect
            LaunchedEffect(uiState.shouldUseFirebaseUi, auth.currentUser) {
                if (uiState.shouldUseFirebaseUi && !uiState.isLoading && auth.currentUser == null) {
                    isAuthenticating = true
                    val providers = arrayListOf(
                        com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder().build(),
                        com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder().build()
                    )
                    val signInIntent = com.firebase.ui.auth.AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.ic_launcher_new_foreground)
                        .build()

                    firebaseUiLauncher.launch(signInIntent)
                }
            }

            // 4. UI: Solo mostramos loading o la pantalla normal, NUNCA lanzamos intents aquí
            if (uiState.isLoading || isAuthenticating) {
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            } else {
                // Si no estamos cargando y no se debe usar FirebaseUI automático, mostramos tu pantalla custom
                dev.leotoloza.avengersapp.ui.screens.authentication.AuthenticationScreen(
                    onLoginSuccess = onLoginSuccess
                )
            }
        }

        // Main app graph contiene las pantallas principales de la bottomBar
        navigation(startDestination = CHARACTERS_GRAPH_ROUTE, route = MAIN_APP_GRAPH_ROUTE) {
            // Characters graph anidado para optimizacion del ciclo de vida del ViewModel
            // y que no se recomponga siempre que se navegue a otra pantalla desde la bottomBar.
            // Mantiene los datos que trae de la api cargados como cache.
            navigation(
                startDestination = Screens.Characters.route, route = CHARACTERS_GRAPH_ROUTE
            ) {
                composable(Screens.Characters.route) { entry ->
                    onTitleChange("Marvel Challenge")
                    // CharactersViewModel con alcance en CHARACTERS_GRAPH_ROUTE
                    val parentEntry = remember(entry) {
                        navController.getBackStackEntry(CHARACTERS_GRAPH_ROUTE)
                    }
                    val charactersViewModel: CharactersViewModel = hiltViewModel(parentEntry)
                    CharactersScreen(
                        navController = navController,
                        viewModel = charactersViewModel,
                        snackbarHostState = snackbarHostState
                    )
                }
                composable(
                    route = Screens.CharacterDetail.route,
                    arguments = listOf(navArgument(Screens.CharacterDetail.NAV_ARG_CHARACTER_ID) {
                        type = NavType.LongType
                    })
                ) { entry ->
                    val parentEntry = remember(entry) {
                        navController.getBackStackEntry(CHARACTERS_GRAPH_ROUTE)
                    }
                    val charactersViewModel: CharactersViewModel = hiltViewModel(parentEntry)
                    val selectedCharacterId =
                        entry.arguments?.getLong(Screens.CharacterDetail.NAV_ARG_CHARACTER_ID)
                    val character: Character? =
                        selectedCharacterId?.let { charactersViewModel.getCharacterById(it) }

                    character?.let { char ->
                        onTitleChange(char.name.uppercase())
                        LaunchedEffect(Unit) {
                            charactersViewModel.logCharacterViewed(char)
                        }
                        CharacterDetailScreen(character = char)
                    } ?: run { // Si character es null (id no valido), navega hacia atras
                        LaunchedEffect(Unit) {
                            navController.navigateUp()
                        }
                    }
                }
            }

            // EventsScreen es parte del grafo principal, no del de characters
            composable(Screens.PanelControl.route) { entry ->
                val panelControlViewModel: PanelControlViewModel = hiltViewModel(entry)
                PanelControlScreen(viewModel = panelControlViewModel, onNavigateToFavorites = {
                    navController.navigate(Screens.Favorites.route)
                }, onLogout = {
                    navController.navigate(Screens.Authentication.route) {
                        popUpTo(MAIN_APP_GRAPH_ROUTE) {
                            inclusive = true
                        }
                    }
                })
            }

            composable(Screens.Favorites.route) { entry ->
                onTitleChange("Favoritos")
                val favoritesViewModel: FavoritesViewModel = hiltViewModel(entry)
                FavoritesScreen(
                    viewModel = favoritesViewModel
                )
            }
        }
    }
}