package ui.screens.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.readByteArray
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.feed.FeedId
import moe.tlaster.precompose.koin.koinViewModel
import org.koin.core.parameter.parametersOf
import ui.rememberBitmapFromBytes

@Composable
fun PostScreen(
    challengeId: String?,
    onNavigateUp: () -> Unit,
    onPostCreated: (feedId: FeedId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = koinViewModel(PostViewModel::class) { parametersOf(challengeId) },
) {
    val state = viewModel.uiState.collectAsState()

    println("postscreen")

    PostScreen(
        challenge = state.value.challenge,
        modifier = modifier,
        textContent = state.value.textContent,
        formState = state.value.formState,
        onNavigateUp = onNavigateUp,
        onPostCreated = onPostCreated,
        mediaContent = state.value.mediaContent,
        onTextContentChange = viewModel::onTextContentChange,
        onMediaContentChange = viewModel::onMediaContentChange,
        onPublish = viewModel::onPublish,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    challenge: Challenge? = null,
    modifier: Modifier,
    onNavigateUp: () -> Unit,
    onPostCreated: (feedId: FeedId) -> Unit,
    textContent: String,
    formState: FormState,
    mediaContent: List<KmpFile>?,
    onTextContentChange: (String) -> Unit,
    onMediaContentChange: (List<KmpFile>?) -> Unit,
    onPublish: () -> Unit,
) {
    val snackHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackHostState) },
        topBar = {
            TopAppBar(
                title = { if (challenge != null) Text("Submission to ${challenge.title}") else Text("Post") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onPublish, enabled = formState == FormState.Valid) {
                        Icon(
                            Icons.Outlined.Send,
                            contentDescription = "Publish"
                        )
                    }
                }
            )
        },
        bottomBar = {
            val pickerLauncher = rememberFilePickerLauncher(
                type = FilePickerFileType.Image,
                selectionMode = FilePickerSelectionMode.Multiple
            ) { files ->
                onMediaContentChange(files)
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                if (!mediaContent.isNullOrEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(mediaContent) { file ->
                            val imageBitmap = rememberBitmapFromBytes(file.readByteArray())
                            if (imageBitmap != null) {
                                Box {
                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = "Image",
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .width(100.dp)
                                            .height(100.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                }
                Row(
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { }, enabled = false) {
                        Icon(
                            Icons.Outlined.Public,
                            contentDescription = "Privacy Settings",
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("This post would be visible to everyone")
                    }
                }

                BottomAppBar(
                    actions = {
                        IconButton(onClick = { pickerLauncher.launch() }) {
                            Icon(
                                Icons.Outlined.LibraryAdd,
                                contentDescription = "Add Image",
                            )
                        }
                        IconButton(onClick = { }, enabled = false) {
                            Icon(
                                Icons.Outlined.LocationOn,
                                contentDescription = "Add Location",
                            )
                        }
                    },
                )
            }
        },
    ) {
        Column(
            modifier = Modifier.padding(it).padding(8.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                enabled = formState != FormState.Submitting,
                value = textContent,
                onValueChange = onTextContentChange,
                placeholder = { Text("How did you make the world greener?") },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    LaunchedEffect(formState) {
        when (formState) {
            is FormState.Error -> snackHostState.showSnackbar(formState.message)
            is FormState.Submitted -> onPostCreated(formState.feedId)
            else -> {}
        }
    }
}

