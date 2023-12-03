package rs.elfak.climb.ui.screens.personalize

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import rs.elfak.climb.components.TopBar
import rs.elfak.climb.core.Constants
import rs.elfak.climb.core.Utils
import rs.elfak.climb.ui.screens.personalize.components.Personalize
import rs.elfak.climb.ui.screens.personalize.components.PersonalizeContent
import rs.elfak.climb.ui.screens.profile.ProfileViewModel
import rs.elfak.climb.ui.screens.sign_in.components.SignIn

@Composable
fun PersonalizeScreen(
    personalizeViewModel: PersonalizeViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopBar(
                title = Constants.PERSONALIZE_SCREEN,
                signOut = {
                    profileViewModel.signOut()
                },
                revokeAccess = {
                    profileViewModel.revokeAccess()
                }
            )
        },
        content = { 
            PersonalizeContent(
                padding = it,
                updateUser = { username, fullName, phone, bitmap ->
                    personalizeViewModel.updateUser(username, fullName, phone, bitmap)
                },
            )
        }
    )

    Personalize(
        showErrorMessage = { errorMessage ->
            Utils.showMessage(context, errorMessage)
        },
        onSuccess = { navigateToHome() }
    )
}