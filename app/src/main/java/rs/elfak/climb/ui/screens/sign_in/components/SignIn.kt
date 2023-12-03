package rs.elfak.climb.ui.screens.sign_in.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import rs.elfak.climb.components.ProgressBar
import rs.elfak.climb.data.model.Response
import rs.elfak.climb.ui.screens.sign_in.SignInViewModel

@Composable
fun SignIn(
    viewModel: SignInViewModel = hiltViewModel(),
    showErrorMessage: (errorMessage: String?) -> Unit,
    navigateToHome: () -> Unit
) {
    when(val signInResponse = viewModel.signInResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> {
            LaunchedEffect(Unit) {
                navigateToHome()
            }
        }
        is Response.Failure -> signInResponse.apply {
            LaunchedEffect(e) {
                print(e)
                showErrorMessage(e.message)
            }
        }
        else -> {}
    }
}