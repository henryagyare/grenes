package ui.screens.challenges

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.challenges.ChallengeDifficulty

@Preview
@Composable
private fun ChallengesScreenPreview() {
//    ChallengesScreenUi(modifier = Modifier, selectedTabIndex = 0, onTabSelected = {})
    val challenge = Challenge(
        isTrackable = true,
        isActive = true,
        difficulty = ChallengeDifficulty.MEDIUM,
        createdAt = Clock.System.now(),
        challengeTypes = emptyList(),
        uid = "894398hjsdhjds87",
        description = "Silly description here",
        suggestedBy = null,
        title = "Silly Title"
    )

    ChallengesScreenUi(
        dailyChallenges = listOf(challenge, challenge, challenge),
        monthlyChallenges = listOf(challenge),
        navigateToChallengeDetails = {},
        modifier = Modifier, selectedTabIndex = 0, onTabSelected = {}
    )
}

@Preview
@Composable
fun MonthlyChallengePreview() {
//    MonthlyChallenge()
}

@Preview
@Composable
fun DailyChallengePreview() {
//    DailyChallenge()
}