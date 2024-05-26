package ui.screens.user

sealed class ProfileScreenEvent {
    data object OnFollowClick : ProfileScreenEvent()
    data object OnUnFollowClick : ProfileScreenEvent()
    data object OnEditProfileClick : ProfileScreenEvent()
    data object OnBackClick : ProfileScreenEvent()
    data object OnSettingsClick : ProfileScreenEvent()
    data object OnLogoutClick : ProfileScreenEvent()
    data object OnFollowersClick : ProfileScreenEvent()
    data object OnFollowingClick : ProfileScreenEvent()
    data object OnPostsClick : ProfileScreenEvent()
    data object OnPostClick : ProfileScreenEvent()
    data object OnRetryClick : ProfileScreenEvent()
}