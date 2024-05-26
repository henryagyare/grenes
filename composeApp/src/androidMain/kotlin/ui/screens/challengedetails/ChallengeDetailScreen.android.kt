package ui.screens.challengedetails

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.challenges.ChallengeDifficulty

@Preview
@Composable
fun ChallengeDetailScreenPreview() {
    val challenge = Challenge(
        isTrackable = true,
        isActive = true,
        difficulty = ChallengeDifficulty.MEDIUM,
        createdAt = Clock.System.now(),
        challengeTypes = emptyList(),
        uid = "894398hjsdhjds87",
        description = "Silly description or manybe ebven more text here",
        suggestedBy = null,
        title = "Silly Title"
    )

    ChallengeDetailScreen(
        navigateBack = {},
        modifier = Modifier,
        onMakeSubmission = {},
        challenge = challenge,
    )
}