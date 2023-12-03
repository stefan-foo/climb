package rs.elfak.climb.ui.screens.map.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import rs.elfak.climb.components.ProgressBar
import rs.elfak.climb.data.model.Response
import rs.elfak.climb.ui.screens.map.MapViewModel

@Composable
fun CreateTrack(
    viewModel: MapViewModel = hiltViewModel(),
    onSuccess: () -> Unit
) {
    when(val signUpResponse = viewModel.pathCreationResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> {
            LaunchedEffect(Unit) {
                onSuccess()
                viewModel.resetRequestState()
            }
        }
        is Response.Failure -> signUpResponse.apply {
            LaunchedEffect(e) {
                print(e)
            }
        }
        is Response.Idle -> { }
    }
}