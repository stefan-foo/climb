package rs.elfak.climb.ui.screens.map.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsBike
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.Hiking
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.elfak.climb.R
import rs.elfak.climb.components.ClimbAsyncImage
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.enums.PathCategory
import rs.elfak.climb.data.model.Track

@Composable
fun TrackCard(
    distance: Int,
    track: Track,
    showDistance: Boolean = true,
    navigateToTrack: (trackId: String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable {
                navigateToTrack(track.id)
            },
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (showDistance) {
                Text(
                    text = "Distance from you: ${Utils.formatDistance(distance.toLong())}",
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                    fontSize = 14.sp
                )
            }
            ListItem(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                leadingContent = { ClimbAsyncImage(
                    imageUrl = track.imageUri,
                    modifier = Modifier.size(64.dp),
                    contentDescription = null,
                    resourceId = R.drawable.mountainplaceholder,
                ) },
                headlineContent = {
                    Text(track.trackName)
                },
                trailingContent = {
                    Icon(
                        contentDescription = null,
                        imageVector = when(track.category) {
                            PathCategory.HIKING -> Icons.Rounded.Hiking
                            PathCategory.WALKING -> Icons.Rounded.DirectionsWalk
                            else -> Icons.Rounded.DirectionsBike
                        },
                        modifier = Modifier.size(48.dp),
                        tint = when(track.category) {
                            PathCategory.HIKING -> Color(1, 141, 54)
                            PathCategory.WALKING -> Color(100, 58, 107)
                            else -> Color(195, 129, 84)
                        }
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun ldbItem() {
    TrackCard(track = Track(trackName = "gyros track"), distance = 0, navigateToTrack = {})
}