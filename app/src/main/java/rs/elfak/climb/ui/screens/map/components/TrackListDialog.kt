package rs.elfak.climb.ui.screens.map.components

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.model.LatLng
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.model.Track
import kotlin.math.abs

@Composable
fun TrackListDialog(
    location: LatLng?,
    tracks: List<Track>,
    onDismiss: () -> Unit,
    navigateToTrack: (trackId: String) -> Unit
) {
    val sortedTracks = remember(tracks) {
        if (location == null) {
            tracks.map { Pair(it, 0) }
        } else {
            tracks
                .map { it ->
                    Pair(it, Utils.calculateDistance(location, it.startingPoint))
                }
                .sortedBy {
                    it.second
                }
        }
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            modifier = Modifier.pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    Log.d("ClimbApp", "drag")
                    val (x, y) = dragAmount

                    if (x != 0F && abs(x) > abs(y)) {
                        onDismiss()
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth()
                ) {
                    items(sortedTracks) {item ->
                        TrackCard(item.second.toInt(), item.first, showDistance = location != null, navigateToTrack = navigateToTrack)
                    }
                }
            }
        }
    }
}