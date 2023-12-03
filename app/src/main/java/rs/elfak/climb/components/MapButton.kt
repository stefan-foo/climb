package rs.elfak.climb.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.MyLocation
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun MapButton(modifier: Modifier = Modifier
    .fillMaxSize()
    .padding(bottom = 125.dp, end = 12.dp)
) {
//    Box(
//        modifier = Modifier
//        .fillMaxSize()
//        .padding(bottom = 125.dp, end = 12.dp)
//    ) {
//        SmallFloatingActionButton(
//            onClick = {
//                scope.launch {
//                    val currentLoc = currentLocation.value
//                    if (currentLoc != null) {
//                        cameraPositionState.centerOnLocation(currentLoc)
//                    }
//                }
//            },
//            elevation = FloatingActionButtonDefaults.elevation(0.dp),
//            shape = CircleShape,
//            modifier = Modifier
//                .size(40.dp)
//                .align(Alignment.BottomEnd),
//            containerColor = Color.White.copy(alpha = 0.5F),
//            content = {
//                Icon(
//                    modifier = Modifier.size(25.dp),
//                    imageVector = Icons.Sharp.MyLocation,
//                    contentDescription = "Location",
//                    tint = Color.DarkGray
//                )
//            }
//        )
//    }
}