package rs.elfak.climb.ui.screens.sign_up.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import rs.elfak.climb.ui.screens.sign_up.SignUpViewModel
import rs.elfak.climb.components.ProgressBar
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.model.Response

@Composable
fun SignUp(
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateToPersonalize: () -> Unit,
    context: Context
) {
    when(val signUpResponse = viewModel.signUpResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> {
            navigateToPersonalize()
        }
        is Response.Failure -> signUpResponse.apply {
            LaunchedEffect(e) {
                print(e)
                Utils.showMessage(context, e.toString())
            }
        }
        else -> {}
    }
}