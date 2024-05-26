package ui.screens.challengedetails

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.ayitinya.grenes.data.challenges.Challenge
import moe.tlaster.precompose.koin.koinViewModel
import org.koin.core.parameter.parametersOf
import ui.catalog.Loading

@Composable
fun ChallengeDetailScreen(
    uid: String,
    navigateBack: () -> Unit,
    onMakeSubmission: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChallengeDetailViewModel = koinViewModel(ChallengeDetailViewModel::class) {
        parametersOf(
            uid
        )
    },
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.loading) {
        false -> {
            ChallengeDetailScreen(
                navigateBack = navigateBack,
                onMakeSubmission = onMakeSubmission,
                modifier = modifier,
                challenge = uiState.challenge!!
            )
        }

        true -> Loading(modifier = Modifier.fillMaxSize())
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailScreen(
    navigateBack: () -> Unit,
    onMakeSubmission: () -> Unit,
    modifier: Modifier = Modifier,
    challenge: Challenge,
) {
    var moreDropdownMenuOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }

                    Box {
                        IconButton(onClick = { moreDropdownMenuOpen = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More"
                            )
                        }

                        DropdownMenu(
                            expanded = moreDropdownMenuOpen,
                            onDismissRequest = { moreDropdownMenuOpen = false },

                            ) {
                            DropdownMenuItem(text = { Text("Report a problem") }, onClick = {})
                        }
                    }

                },
                title = {
                    Text(text = "Challenge Detail", style = MaterialTheme.typography.titleLarge)
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onMakeSubmission) {
                Text(text = "Make A Submission")
            }
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 8.dp).fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = challenge.description)
        }
    }
}

