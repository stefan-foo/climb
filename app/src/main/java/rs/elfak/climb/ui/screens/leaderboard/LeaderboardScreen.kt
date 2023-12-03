package rs.elfak.climb.ui.screens.leaderboard

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.elfak.climb.core.Constants
import rs.elfak.climb.ui.screens.leaderboard.components.LeaderboardContent
import android.util.Log
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardModelView = hiltViewModel(),
) {
    val users by viewModel.getUsersData().collectAsStateWithLifecycle()

    Log.d(Constants.TAG, "SCREEN ${users.size}")

    Scaffold(
        topBar = {
            TopAppBar (
                title = {
                    Text(
                        text = Constants.LEADERBOARD_SCREEN
                    )
                }
            )
        },
        content = { padding ->
            LeaderboardContent(paddingValues = padding, users = users)
        },
    )
}