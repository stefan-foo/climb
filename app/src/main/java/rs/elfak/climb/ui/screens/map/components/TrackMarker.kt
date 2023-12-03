package rs.elfak.climb.ui.screens.map.components

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import rs.elfak.climb.R
import rs.elfak.climb.data.enums.PathCategory
import rs.elfak.climb.data.model.Track
import android.util.Log
import rs.elfak.climb.core.Constants

@Composable
fun TrackMarker(track: Track, onClick: () -> Unit = { }) {
    val icon = when (track.category) {
        PathCategory.WALKING -> R.drawable.walking
        PathCategory.CYCLING -> R.drawable.cycling
        PathCategory.HIKING -> R.drawable.hiking
    }

    Marker(
        state = MarkerState(position = track.startingPoint),
        title = track.trackName,
        icon = BitmapDescriptorFactory.fromResource(icon),
        onClick = {
            Log.d(Constants.TAG, "clicked onclick")
            onClick()
            true
        },
    )
}


