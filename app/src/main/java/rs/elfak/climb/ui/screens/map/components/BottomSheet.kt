package rs.elfak.climb.ui.screens.map.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.annotations.Async
import rs.elfak.climb.components.MultipleChipSelector
import rs.elfak.climb.components.Spacer8
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.enums.PathCategory
import rs.elfak.climb.data.model.User
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DirectionsBike
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.Hiking
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import rs.elfak.climb.components.ClimbAsyncImage
import rs.elfak.climb.components.ClimbImage
import rs.elfak.climb.core.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    onFilter: (
        radiusKms: Int,
        trackLengthRange: ClosedFloatingPointRange<Float>,
        selectedTrackCategories: List<PathCategory>,
        addedBy: User?
    ) -> Unit,
    users: List<User>
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var radius by remember { mutableStateOf(100) }
    var trackLengthRange by remember { mutableStateOf(0f..10000f) }
    var selectedTrackCategories by remember { mutableStateOf<List<PathCategory>>(listOf()) }
    var pickedUser by remember { mutableStateOf<User?>(null) }
    val filteredUsers = remember { mutableStateListOf<User>() }

    LaunchedEffect(query, users)  {
        val queryTrimmed = query.trim()

        if (queryTrimmed.isEmpty()) {
            filteredUsers.clear()
            filteredUsers.addAll(users.take(3))
        } else if (queryTrimmed.length > 2) {
            filteredUsers.clear()

            filteredUsers.addAll(
                users.asSequence()
                    .filter {
                        it.email.startsWith(prefix = queryTrimmed, ignoreCase = true) ||
                        it.username.startsWith(prefix = queryTrimmed, ignoreCase = true)
                    }
                    .take(3)
                    .sortedBy { it.username }
                    .toList()
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DockedSearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = query,
            onQueryChange = {
                query = it
            },
            onSearch = { active = false },
            active = active,
            onActiveChange = {
                active = it
            },
            placeholder = { Text(Strings.SEARCH_USER) },
            leadingIcon = {
                if (active) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable() {
                                active = false
                                query = ""
                            },
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            },
        ) {
            if (filteredUsers.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredUsers) {user ->
                        ListItem(
                            modifier = Modifier.clickable {
                                active = false
                                pickedUser = user
                            },
                            headlineContent = { Text(text = user.username) },
                            supportingContent = { Text(text = user.email ) },
                            leadingContent = {
                                AsyncImage(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(34.dp),
                                    model = user.image,
                                    placeholder = rememberVectorPainter(image = Icons.Rounded.AccountCircle),
                                    fallback = rememberVectorPainter(image = Icons.Rounded.AccountCircle),
                                    error = rememberVectorPainter(image = Icons.Rounded.AccountCircle),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        }

        pickedUser?.let {user ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer8()

                Text(text = Strings.TRACKS_OF_USER)

                Spacer8()

                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            ClimbAsyncImage(
                                modifier = Modifier.size(34.dp),
                                imageUrl = user.image,
                                contentDescription = null,
                                resourceId = null,
                                placeholderAlt = {
                                    Icon(imageVector = Icons.Rounded.AccountCircle, contentDescription = null)
                                }
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(text = user.username, fontSize = 18.sp)
                                Text(text = user.email, fontSize = 16.sp)
                            }
                        }

                        IconButton(
                            onClick = { pickedUser = null },
                            modifier = Modifier.size(32.dp),
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.Black
                                )
                            }
                        )
                    }
                }
            }
        }

        Spacer8()


        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(Strings.RADIUS)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "0km")
                Text(text = "500km")
            }

            Slider(
                value = radius.toFloat(),
                onValueChange = { radius = it.toInt() },
                valueRange = 0f..500f,
                steps = 100
            )

            Text(text = "$radius km")
        }

        Spacer8()

        Text(Strings.TRACK_LENGTH)

        RangeSlider(
            value = trackLengthRange,
            onValueChange = { trackLengthRange = it },
            valueRange = 0f..10000f,
            steps = 100
        )

        Text(text = "${trackLengthRange.start.toInt()}m - ${trackLengthRange.endInclusive.toInt()}m")

        Spacer8()

        MultipleChipSelector(
            modifier = Modifier,
            items = PathCategory.values().toList(),
            value = selectedTrackCategories,
            onSelectionChange = { selectedTrackCategories = it },
            display = { it.name },
            trailingIcons = PathCategory.values().map {
                when(it) {
                    PathCategory.WALKING -> Icons.Rounded.DirectionsWalk
                    PathCategory.HIKING -> Icons.Rounded.Hiking
                    else -> Icons.Rounded.DirectionsBike
                }
            }
        )

        Spacer8()

        Button(onClick = { onFilter(radius, trackLengthRange, selectedTrackCategories, pickedUser) }) {
            Text(Strings.APPLY_FILTERS)
        }

        Spacer8()
    }
}