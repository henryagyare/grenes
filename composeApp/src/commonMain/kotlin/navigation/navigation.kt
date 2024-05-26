package navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import data.auth.AuthState
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.rememberNavigator

@Composable
fun Nav(
    sharedViewModel: SharedViewModel = koinViewModel(SharedViewModel::class),
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val navigator = rememberNavigator()

    val state = sharedViewModel.uiState.collectAsState()

    when {
        state.value.initializing -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        else -> {
            val initialRoute: String = when (state.value.authState) {
                AuthState.Anonymous -> Screens.MainNavigation.Feed.path

                is AuthState.Authenticated -> {
                    if (state.value.setupComplete) {
                        Screens.MainNavigation.Feed.path
                    } else Screens.Onboarding.Profile.path
                }

                AuthState.NotAuthenticated -> Screens.Onboarding.Landing.path

            }

            val snackHostState = remember { SnackbarHostState() }

            Scaffold(
                bottomBar = {
                    val navBackStackEntry = navigator.currentEntry.collectAsState(initial = null)

                    val currentDestination = navBackStackEntry.value?.route?.route

                    if ((state.value.authState is AuthState.Authenticated || state.value.authState is AuthState.Anonymous) && currentDestination?.contains(
                            "onboarding"
                        ) == false
                    ) {

                        val isMainNavigationRoute =
                            Screens.navScreens.any { it.path == currentDestination }

                        if (isMainNavigationRoute) {
                            NavigationBar {
                                Screens.navScreens.forEach { screen ->
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = screen.icon,
                                                contentDescription = screen.label,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        },
                                        selected = currentDestination == screen.path,
                                        onClick = {
                                            if (currentDestination != screen.path) {
                                                navigator.navigate(
                                                    route = if (screen is Screens.MainNavigation.Profile) {
                                                        Screens.MainNavigation.profileRoute((state.value.authState as AuthState.Authenticated).user.uid.value)
                                                    } else
                                                        screen.path
                                                )
                                            }
                                        })
                                }
                            }
                        }
                    }
                },
                snackbarHost = { SnackbarHost(snackHostState) },
                modifier = modifier
            ) { paddingValues ->
                GrenesNavHost(paddingValues, navigator, initialRoute, snackHostState)
            }

//            LaunchedEffect(state.value.authState) {
//                /*
//                TODO:
//                Might have to check network connection
//                Or notify user of account status change hence re login required
//                 */
//                if (state.value.authState == AuthState.NotAuthenticated) {
//                    navigator.navigate(Screens.Onboarding.Landing.path)
//                }
//            }
        }
    }
}
