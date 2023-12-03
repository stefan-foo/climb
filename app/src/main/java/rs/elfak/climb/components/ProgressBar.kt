package rs.elfak.climb.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

@Composable
fun ProgressBar() {
    Box(
        modifier = Modifier.fillMaxSize().zIndex(1000F),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}