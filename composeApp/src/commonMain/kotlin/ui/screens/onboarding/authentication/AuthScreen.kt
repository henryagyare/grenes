package ui.screens.onboarding.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthScreenViewModel = koinViewModel(AuthScreenViewModel::class),
    navigateToOnboarding: () -> Unit,
    navigateToFeed: () -> Unit,
    onBackButtonClick: () -> Unit,
) {
    val state = viewModel.uiState


    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier.fillMaxHeight(0.3f).fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBackButtonClick,
                modifier = Modifier.align(Alignment.TopStart).windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Icon(imageVector = Icons.Default.ArrowLeft, contentDescription = "Back")
            }
            Text("Large landing image here", color = MaterialTheme.colorScheme.onSecondaryContainer)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Continue with email", style = MaterialTheme.typography.headlineLarge)
                Text("Please enter your email to continue")

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    enabled = true,
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if ((state.authFlowState is AuthFlowState.UserDoesNotExist || state.authFlowState is AuthFlowState.UserExist)) {
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }

            Button(
                enabled = state.email.isNotEmpty() && state.authFlowState !is AuthFlowState.Loading,

                onClick = {
                    when (state.authFlowState) {
                        is AuthFlowState.Idle -> {
                            viewModel.fetchSignInMethodsForEmail()
                        }

                        is AuthFlowState.UserExist -> {
                            viewModel.signInWithEmailAndPassword()
                        }

                        is AuthFlowState.UserDoesNotExist -> {
                            viewModel.createUserWithEmailAndPassword()
                        }

                        else -> {
                            viewModel.retry()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    if (state.authFlowState is AuthFlowState.Idle) "Continue" else if (state.authFlowState is AuthFlowState.UserExist) "Login" else "Continue",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Icon(imageVector = Icons.Default.ArrowRightAlt, contentDescription = "Continue")
            }
        }
    }

    val showSnackbar by remember { mutableStateOf(false) }

    if (showSnackbar) {
        Snackbar(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                when (state.authFlowState) {
                    is AuthFlowState.UserLoggedIn -> "Logged in successfully"
                    is AuthFlowState.UserCreated -> "User created successfully"
                    AuthFlowState.EmailSent -> "Email sent successfully"
                    is AuthFlowState.Error -> (state.authFlowState as AuthFlowState.Error).message
                    AuthFlowState.Idle -> ""
                    AuthFlowState.Loading -> "Loading..."
                    AuthFlowState.UserDoesNotExist -> "User does not exist"
                    is AuthFlowState.UserExist -> "User already exist"
                }
            )
        }
    }

    LaunchedEffect(state.authFlowState) {
        when (state.authFlowState) {
            is AuthFlowState.UserLoggedIn -> {
                if ((state.authFlowState as AuthFlowState.UserLoggedIn).isSetupComplete) {
                    navigateToFeed()
                } else {
                    navigateToOnboarding()
                }
            }

            AuthFlowState.UserCreated -> navigateToOnboarding()


            AuthFlowState.EmailSent -> {}
            is AuthFlowState.Error -> TODO()
            AuthFlowState.Idle -> {}
            AuthFlowState.Loading -> {}
            AuthFlowState.UserDoesNotExist -> {}
            is AuthFlowState.UserExist -> {}
        }
    }

}