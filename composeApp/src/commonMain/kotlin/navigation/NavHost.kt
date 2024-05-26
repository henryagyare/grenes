package navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.users.UserId
import moe.tlaster.precompose.navigation.*
import moe.tlaster.precompose.navigation.transition.NavTransition
import ui.screens.challengedetails.ChallengeDetailScreen
import ui.screens.challenges.ChallengesScreenUi
import ui.screens.feeddetails.FeedDetail
import ui.screens.home.HomeScreenUi
import ui.screens.onboarding.LandingScreenUi
import ui.screens.onboarding.authentication.AuthScreen
import ui.screens.onboarding.googleSignIn
import ui.screens.onboarding.profile.ProfileScreen
import ui.screens.post.PostScreen
import ui.screens.user.UserScreen


@Composable
internal fun GrenesNavHost(
    paddingValues: PaddingValues,
    navigator: Navigator,
    initialRoute: String,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    NavHost(
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
        navigator = navigator,
        navTransition = NavTransition(),
        initialRoute = initialRoute,
    ) {
        scene(Screens.MainNavigation.Feed.path) {
            HomeScreenUi(
                navigateToFeed = {
                    navigator.navigate(Screens.FeedDetail.path.replace("{feedId}", it.value))
                },
                makeSubmission = {
                    navigator.navigate(
                        Screens.CreatePost.path.replace(
                            "/{challengeId}?",
                            ""
                        )
                    )
                }, modifier = Modifier.fillMaxSize()
            )
        }

        scene(Screens.MainNavigation.Profile.path) { backStackEntry ->
            val uid: String? = backStackEntry.path<String>("uid")

            uid?.let {
                UserScreen(uid = UserId(uid), modifier = Modifier.fillMaxSize())
            }
        }

        scene(Screens.Onboarding.Profile.path) {
            ProfileScreen(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            ) {
                navigator.navigate(Screens.MainNavigation.Feed.path)
            }
        }

        scene(Screens.Onboarding.Landing.path) {
            LandingScreenUi(modifier = Modifier.fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars),
                onGoogleButtonClick = {
                    val res = googleSignIn()

                    if (res.value.isNotEmpty()) {
                        navigator.navigate(Screens.Onboarding.Profile.path)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Google Sign In Failed")
                        }
                    }
                },
                onEmailButtonClick = { navigator.navigate(Screens.Onboarding.Auth.path) })
        }

        scene(Screens.Onboarding.Auth.path) {
            AuthScreen(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.navigationBars),
                navigateToOnboarding = {
                    navigator.navigate(Screens.Onboarding.Profile.path)
                },
                navigateToFeed = {
                    navigator.navigate(Screens.MainNavigation.Feed.path)
                },
                onBackButtonClick = navigator::goBack
            )
        }

        scene(Screens.MainNavigation.Challenges.path) {
            ChallengesScreenUi(modifier = Modifier.fillMaxSize(), navigateToChallengeDetails = {
                navigator.navigate(Screens.ChallengeDetail.path.replace("{uid}", it))
            })
        }

        scene(Screens.ChallengeDetail.path) { backStackEntry ->
            val uid: String? = backStackEntry.path<String>("uid")

            uid?.let {
                ChallengeDetailScreen(
                    uid = uid,
                    modifier = Modifier.fillMaxSize(),
                    onMakeSubmission = {
                        navigator.navigate(Screens.CreatePost.path.replace("{challengeId}?", uid))
                    },
                    navigateBack = navigator::goBack
                )
            }
        }

        scene(Screens.CreatePost.path) { backStackEntry ->
            val challengeId: String? = backStackEntry.path<String>("challengeId")

            PostScreen(
                challengeId = challengeId,
                onNavigateUp = navigator::goBack,
                onPostCreated = {
                    navigator.navigate(
                        route = Screens.FeedDetail.path.replace("{feedId}", it.value),
                        options = NavOptions(popUpTo = PopUpTo(Screens.MainNavigation.Feed.path))
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        scene(Screens.FeedDetail.path) { backStackEntry ->
            val feedId: String? = backStackEntry.path<String>("feedId")

            feedId?.let {
                FeedDetail(
                    feedId = FeedId(it),
                    onNavigateUp = navigator::goBack,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}