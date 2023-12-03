package rs.elfak.climb.components

import android.content.Context
import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import rs.elfak.climb.data.model.User

@Composable
fun UserMarker(
    user: User
) {
    user.lastKnownLocation?.let {
        Marker(
            state = MarkerState(position = LatLng(it.latitude, it.longitude)),
            title = user.username,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        )
    }
}
