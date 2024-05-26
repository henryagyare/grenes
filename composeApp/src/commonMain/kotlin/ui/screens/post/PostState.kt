package ui.screens.post

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mohamedrejeb.calf.io.KmpFile
import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.feed.FeedId

internal data class PostState(
    val textContent: String = "",
    val mediaContent: SnapshotStateList<KmpFile> = mutableStateListOf(),
    val challenge: Challenge? = null,
    val loading: Boolean = true,
    val formState: FormState = FormState.Invalid,
)

sealed class FormState {
    data class Submitted(val feedId: FeedId) : FormState()

    data object Invalid : FormState()

    data object Submitting : FormState()

    data object Valid : FormState()

    data class Error(val message: String) : FormState()
}