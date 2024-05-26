package ui.screens.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.catalog.Loading
import ui.screens.State

@Preview
@Composable
fun UserScreenPreview() {
    UserScreen(
        modifier = Modifier,
        isOwnUser = true,
        username = "ayitinya",
        displayName = "Rudy Ayitinya",
        posts = State.Loading(),
        badges = State.Loading()
    )
}

@Preview
@Composable
fun LoadingPreview() {
    Loading(modifier = Modifier)
}
