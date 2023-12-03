package rs.elfak.climb.ui.screens.sign_in

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import rs.elfak.climb.core.Constants
import rs.elfak.climb.core.Utils
import rs.elfak.climb.ui.screens.sign_in.components.SignIn
import rs.elfak.climb.ui.screens.sign_in.components.SignInContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@ExperimentalComposeUiApi
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    navigateToSignUpScreen: () -> Unit,
    navigateToHome: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar (
                title = {
                    Text(
                        text = Constants.SIGN_IN_SCREEN
                    )
                }
            )
        },
        content = { padding ->
            SignInContent(
                padding = padding,
                signIn = { email, password ->
                    viewModel.signInWithEmailAndPassword(email, password)
                },
                navigateToSignUpScreen = navigateToSignUpScreen
            )
        }
    )

    SignIn(
        showErrorMessage = { errorMessage ->
            Utils.showMessage(context, errorMessage)
        },
        navigateToHome = navigateToHome
    )
}