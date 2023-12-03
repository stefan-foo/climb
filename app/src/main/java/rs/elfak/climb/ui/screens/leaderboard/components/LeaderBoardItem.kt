package rs.elfak.climb.ui.screens.leaderboard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import rs.elfak.climb.R
import rs.elfak.climb.components.ClimbAsyncImage
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.model.User

@Composable
fun LeaderboardItem(user: User, position: Int, modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (position <= 2) {
            val id: Int = when (position) {
                0 -> R.drawable.place_1
                1 -> R.drawable.place_2
                else -> R.drawable.place_3
            }

            Image(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = id),
                contentDescription = null
            )
        } else {
            Text(
                text = (position + 1).toString(),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Center
            )
        }

        Card(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClimbAsyncImage(
                        imageUrl = user.image,
                        contentDescription = null,
                        resourceId = null,
                        modifier = Modifier.size(46.dp),
                        placeholderAlt = {
                            Icon(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(46.dp),
                            )
                        }
                    )


                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = user.username)
                        Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Text(Utils.formatDistance(user.distanceCovered), fontSize = 20.sp)
            }
        }
    }
}