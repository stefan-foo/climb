package rs.elfak.climb.ui.screens.personalize.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import rs.elfak.climb.components.ProgressBar
import rs.elfak.climb.data.model.Response
import rs.elfak.climb.ui.screens.personalize.PersonalizeViewModel

@Composable
fun Personalize(
    viewModel: PersonalizeViewModel = hiltViewModel(),
    showErrorMessage: (errorMessage: String?) -> Unit,
    onSuccess: () -> Unit
) {
    when(val personalizeResponse = viewModel.updateUserResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> {
            onSuccess()
        }
        is Response.Failure -> personalizeResponse.apply {
            LaunchedEffect(e) {
                print(e)
                showErrorMessage(e.message)
            }
        }
        else -> {}
    }
}