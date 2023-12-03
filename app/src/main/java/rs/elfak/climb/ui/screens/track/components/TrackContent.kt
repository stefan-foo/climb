package rs.elfak.climb.ui.screens.track.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.elfak.climb.R
import rs.elfak.climb.components.ClimbAsyncImage
import rs.elfak.climb.components.Spacer8
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.model.Track
import rs.elfak.climb.data.model.User
import java.util.Date

@Composable
fun TrackContent(
    paddingValues: PaddingValues,
    track: Track?,
    user: User?,
    likeChange: (newValue: Int?) -> Unit,
    rating: Int,
    likeState: Int?,
    visited: Int?,
    visitorsNum: Int?
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                colors = CardDefaults
                    .cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (user != null) {
                        Spacer8()
                        Text(text = "Created by: ${user.username}")
                    }
                    track?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ClimbAsyncImage(
                                imageUrl = track.imageUri,
                                contentDescription = null,
                                resourceId = R.drawable.mountainplaceholder,
                                modifier = Modifier.size(140.dp),
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Column() {
                                Text("Added: ${Utils.formatDate(track.createdAt, "dd/MM/yyyy h:m a")}", fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Track name: ${track.trackName}", fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Rated difficulty: ${track.difficulty}", fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Length: ${Utils.formatDistance(track.trackLengthMeters.toLong())}", fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Duration to finish: ${Utils.formatTimeFromSeconds(track.durationSeconds)}", fontSize = 12.sp)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        visitorsNum?.let {
                            Text("Number of visitors: $it")
                        }
                        visited?.let {
                            Text("You've visited this track $visited times")
                        }
                        Spacer8()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                contentDescription = "Like button",
                                imageVector = Icons.Rounded.ThumbUp,
                                tint = if (likeState == 1)
                                    MaterialTheme.colorScheme.primary
                                else Color.DarkGray,
                                modifier = Modifier.clickable {
                                    if (likeState == 1) {
                                        likeChange(null)
                                    } else {
                                        likeChange(1)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(rating.toString())
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                contentDescription = "Dislike button",
                                imageVector = Icons.Rounded.ThumbDown,
                                tint = if (likeState == -1)
                                    MaterialTheme.colorScheme.primary
                                else Color.DarkGray,
                                modifier = Modifier.clickable {
                                    if (likeState == -1) {
                                        likeChange(null)
                                    } else {
                                        likeChange(-1)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TrackContentPreview() {
    val userLikes = mutableMapOf<String, Int>()
    userLikes["stefan"] = -1
    TrackContent(track = Track(
        trackName = "Gyros",
        createdAt = Date(),
        userLikes = userLikes
    ),
        user = User(
            username = "Stefan",
            userId = "stefan"
        ),
        paddingValues = PaddingValues(0.dp),
        likeChange = {r -> false},
        rating = 0,
        likeState = null,
        visited = 12,
        visitorsNum = 3
    )
}