package rs.elfak.climb.ui.screens.track

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.elfak.climb.data.model.Track
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Constraints
import androidx.core.provider.FontsContractCompat
import dagger.hilt.android.lifecycle.HiltViewModel
import rs.elfak.climb.components.ProgressBar
import rs.elfak.climb.components.TopBar
import rs.elfak.climb.core.Constants
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.model.Response
import rs.elfak.climb.data.model.User
import rs.elfak.climb.ui.Screen
import rs.elfak.climb.ui.screens.MainViewModel
import rs.elfak.climb.ui.screens.profile.ProfileViewModel
import rs.elfak.climb.ui.screens.track.components.TrackContent
import java.lang.Math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    viewModel: TrackViewModel = hiltViewModel(),
    trackId: String?,
    navigateBack: () -> Unit = {},
) {
    val context = LocalContext.current

    LaunchedEffect(Unit, trackId) {
        trackId?.let {
            viewModel.fetchTrackAndUser(trackId)
        }
    }

    val user by viewModel.getCreator().collectAsStateWithLifecycle()
    val track by viewModel.getTrack().collectAsStateWithLifecycle()
    val likeState by viewModel.getLikeState().collectAsStateWithLifecycle()
    val rating by viewModel.getRatingState().collectAsStateWithLifecycle()
    val visited by viewModel.getVisited().collectAsStateWithLifecycle()
    val visitorsNum by viewModel.numberOfVisitors().collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = Constants.TRACK_SCREEN) },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = Modifier.pointerInput(Unit) {
            detectDragGestures { _, dragAmount ->
                Log.d("ClimbApp", "drag")
                val (x, y) = dragAmount

                if (x != 0F && kotlin.math.abs(x) > kotlin.math.abs(y)) {
                    navigateBack()
                }
            }
        }
    ) {padding ->
        track?.let {
            TrackContent(
                paddingValues = padding,
                track = it,
                user = user,
                rating = rating,
                likeState = likeState,
                visited = visited,
                visitorsNum = visitorsNum,
                likeChange = { value ->
                    track?.let { track ->
                        viewModel.toggleLike(track.id, value)
                    }
                }
            )
        }
    }

    when(val fetchingTrack = viewModel.fetchingTrackResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> { }
        is Response.Failure -> fetchingTrack.apply {
            LaunchedEffect(e) {
                Utils.showMessage(context, e.message)
            }
        }
        else -> {}
    }
}