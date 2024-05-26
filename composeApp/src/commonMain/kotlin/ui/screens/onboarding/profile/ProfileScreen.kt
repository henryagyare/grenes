package ui.screens.onboarding.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingProfileScreenViewModel = koinViewModel(OnboardingProfileScreenViewModel::class),
    onSave: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    ProfileScreen(
        modifier,
        uiState.country,
        uiState.city,
        uiState.displayName,
        viewModel::updateDisplayName,
        viewModel::updateCity,
        viewModel::updateCountry
    ) {
        viewModel.createUserProfile()
        onSave()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    country: String,
    city: String,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(modifier = modifier.padding(8.dp),
        topBar = {
            LargeTopAppBar(
                title = { Text("Profile") },
            )

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.surface)
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(
                    value = displayName,
                    onValueChange = onDisplayNameChange,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = city,
                    onValueChange = onCityChange,
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = country,
                    onValueChange = onCountryChange,
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )

            }

            Button(
                enabled = displayName.isNotEmpty(),
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Save")
            }
        }
    }
}