package rs.elfak.climb.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.GoogleMap
import rs.elfak.climb.components.TopBar
import rs.elfak.climb.core.Constants.PROFILE_SCREEN
import rs.elfak.climb.data.model.User
import rs.elfak.climb.ui.screens.profile.components.ProfileContent
import rs.elfak.climb.ui.screens.profile.components.RevokeAccess

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val user by viewModel.getUser().collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar(
                title = PROFILE_SCREEN,
                signOut = {
                    viewModel.signOut()
                },
                revokeAccess = {
                    viewModel.revokeAccess()
                },
                containerColor = MaterialTheme.colorScheme.background
            )
        },
        content = { padding ->
            user?.let {
                ProfileContent(
                    padding = padding,
                    user = it
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    )

    RevokeAccess(
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        signOut = {
            viewModel.signOut()
        }
    )
}

