package ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import getPlatform
import grenes.composeapp.generated.resources.Res
import io.github.alexzhirkevich.compottie.LottieAnimation
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun LandingScreenUi(
    modifier: Modifier = Modifier,
    onEmailButtonClick: () -> Unit,
    onGoogleButtonClick: @Composable () -> Unit,

    ) {

    var loginViaGoogle by remember { mutableStateOf(false) }

    var showModalBottomSheet by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
            contentAlignment = Alignment.Center
        ) {
            var bytes by remember { mutableStateOf(ByteArray(0)) }

            LaunchedEffect(Unit) {
                bytes = Res.readBytes("files/world.json")
            }

            val jsonString = bytes.decodeToString()
            val composition by rememberLottieComposition(LottieCompositionSpec.JsonString(jsonString))


            LottieAnimation(composition)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "We're glad you're here",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )

                Text(
                    "Some text here telling people what they would achieve here",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Divider(modifier = Modifier.fillMaxWidth())

                Button(onClick = {
                    showModalBottomSheet = true
                }, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
                    Text("Get started", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showModalBottomSheet) {
        ModalBottomSheet(onDismissRequest = {
            showModalBottomSheet = false
        }, sheetState = modalBottomSheetState) {
            Column(
                modifier = Modifier.fillMaxHeight(0.6f).fillMaxWidth().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Get started with Genes",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Please choose how you want to continue setting up your account 😊",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Please visit Grenes privacy policy",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {


                    when {
                        getPlatform().name == "ios" -> {
                            //                        this would not work, at least for now, until the condition is changed
                            Button(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Login, contentDescription = null)
                                Text(
                                    "Continue with Apple ID",
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        getPlatform().name.startsWith("Android") -> {
                            Button(
                                onClick = { loginViaGoogle = true },
                                modifier = Modifier.fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.fa_google),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    "Continue with Google",
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Button(
                        onClick = onEmailButtonClick,
                        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.fa_envelope_regular),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Continue with Email",
                            modifier = Modifier.fillMaxWidth(0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    when {
        loginViaGoogle -> {
            onGoogleButtonClick()
        }
    }
}

