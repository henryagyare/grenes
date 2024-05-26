package ui.screens.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import me.ayitinya.grenes.data.challenges.Challenge
import moe.tlaster.precompose.koin.koinViewModel
import kotlin.time.Duration

@Composable
fun ChallengesScreenUi(
    navigateToChallengeDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChallengesViewModel = koinViewModel(ChallengesViewModel::class),
) {
    val uiState = viewModel.uiState.collectAsState()

    ChallengesScreenUi(
        dailyChallenges = uiState.value.dailyChallenges,
        monthlyChallenges = uiState.value.monthlyChallenges,
        navigateToChallengeDetails = navigateToChallengeDetails,
        modifier = modifier,
        selectedTabIndex = uiState.value.selectedTabIndex,
        onTabSelected = { viewModel.onTabSelected(it) })
}

@Composable
internal fun ChallengesScreenUi(
    dailyChallenges: List<Challenge> = emptyList(),
    monthlyChallenges: List<Challenge> = emptyList(),
    navigateToChallengeDetails: (String) -> Unit,
    modifier: Modifier, selectedTabIndex: Int, onTabSelected: (Int) -> Unit,
) {
    Scaffold(modifier = modifier, topBar = {
        TabRow(
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxWidth(),
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
        ) {
            Tab(text = { Text("Challenges") }, selected = true, onClick = { onTabSelected(0) })
            Tab(text = { Text("Badges") }, selected = false, onClick = { onTabSelected(1) })
        }
    }) {
        when (selectedTabIndex) {
            0 -> ChallengesTab(
                dailyChallenges = dailyChallenges,
                monthlyChallenges = monthlyChallenges,
                navigateToChallengeDetails = navigateToChallengeDetails,
                modifier = Modifier.padding(paddingValues = it).fillMaxWidth()
            )

            1 -> {
                LazyColumn(
                    contentPadding = it,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ListItem(
                            leadingContent = { Text("Image") },
                            headlineContent = { Text("Title") },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChallengesTab(
    dailyChallenges: List<Challenge>,
    monthlyChallenges: List<Challenge>,
    navigateToChallengeDetails: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        monthlyChallenges.forEach { challenge ->
            MonthlyChallenge(challenge = challenge)
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daily Challenges")
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(4.dp),
//                    verticalAlignment = Alignment.Bottom
//                ) {
//                    Icon(imageVector = Icons.Outlined.Timer, contentDescription = null)
//
////                    Text("Time Remaining")
//                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(dailyChallenges) { challenge ->
                    DailyChallenge(
                        challenge = challenge,
                        onChallengeClick = navigateToChallengeDetails
                    )
                }
            }
        }
    }
}

@Composable
internal fun DailyChallenge(challenge: Challenge, onChallengeClick: (String) -> Unit = {}) {
    ListItem(
        modifier = Modifier.clickable { onChallengeClick(challenge.uid) },
        headlineContent = { Text(challenge.title) },
    )
}

@Composable
internal fun MonthlyChallenge(challenge: Challenge) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.background(color = MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.weight(0.6f)) {
                Text("MONTH")
                Text("Title")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Icon(imageVector = Icons.Outlined.Timer, contentDescription = null)
                    Text("Time Remaining")
                }
            }

            Column(modifier = Modifier.weight(0.4f)) {
                Text("Image")
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
        ) {
            Text("Total Points")
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = 0.7f)
        }
    }
}