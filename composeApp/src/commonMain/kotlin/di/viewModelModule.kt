package di

import navigation.SharedViewModel
import org.koin.dsl.module
import ui.screens.challengedetails.ChallengeDetailViewModel
import ui.screens.challenges.ChallengesViewModel
import ui.screens.feeddetails.FeedDetailsViewModel
import ui.screens.home.HomeViewModel
import ui.screens.onboarding.authentication.AuthScreenViewModel
import ui.screens.onboarding.profile.OnboardingProfileScreenViewModel
import ui.screens.post.PostViewModel
import ui.screens.user.ProfileScreenViewModel

val viewModelModule = module {
    single {
        HomeViewModel(get())
    }

    factory {
        OnboardingProfileScreenViewModel(
            usersRepository = get(), authRepository = get(), appPreferences = get()
        )
    }

    factory {
        AuthScreenViewModel(get(), get())
    }

    single(createdAtStart = true) {
        SharedViewModel(authenticationUseCase = get(), appPreferences = get())
    }

    factory { params ->
        ProfileScreenViewModel(
            uid = params.get(),
            usersRepository = get(),
            feedRepository = get(),
            authenticationUseCase = get()
        )
    }

    single {
        ChallengesViewModel(challengesRepository = get())
    }

    factory { params ->
        ChallengeDetailViewModel(challengesRepository = get(), uid = params.get())
    }

    factory { params ->
        PostViewModel(
            usersRepository = get(),
            challengesRepository = get(),
            feedRepository = get(),
            challengeId = params.getOrNull()
        )
    }

    factory { params ->
        FeedDetailsViewModel(
            feedId = params.get(), feedRepository = get(), user = params.get()
        )
    }
}