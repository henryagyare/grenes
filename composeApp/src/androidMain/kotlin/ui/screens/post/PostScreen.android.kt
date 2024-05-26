package ui.screens.post

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.challenges.ChallengeDifficulty

@Composable
@Preview
private fun PostScreenPreview() {
    PostScreen(
        modifier = Modifier.fillMaxSize(),
        onNavigateUp = {},
        onPostCreated = {},
        textContent = "Hello, World!",
        formState = FormState.Valid,
        mediaContent = null,
        onTextContentChange = {},
        onMediaContentChange = {},
        onPublish = {},
    )
}

@Composable
@Preview
private fun PostScreenPreviewWithChallenge() {
    val challenge = Challenge(
        uid = "challengeId",
        title = "Challenge Title",
        description = "Challenge Description",
        challengeTypes = emptyList(),
        createdAt = Clock.System.now(),
        difficulty = ChallengeDifficulty.EASY,
        isActive = true,
        isTrackable = true
    )

    PostScreen(
        challenge = challenge,
        modifier = Modifier.fillMaxSize(),
        onNavigateUp = {},
        onPostCreated = {},
        textContent = "Hello, World!",
        formState = FormState.Valid,
        mediaContent = null,
        onTextContentChange = {},
        onMediaContentChange = {},
        onPublish = {},
    )
}