package rs.elfak.climb.ui.screens.leaderboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import rs.elfak.climb.data.model.User

@Composable
fun LeaderboardContent(users: List<User>, paddingValues: PaddingValues) {
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            itemsIndexed(users) {index, user ->
                LeaderboardItem(user = user, position = index, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}