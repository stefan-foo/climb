package rs.elfak.climb.ui.screens.map.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp

@Composable
fun RecordFAB(isToggled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, alignment: Alignment) {
    Box(modifier = modifier.fillMaxSize()) {
        FloatingActionButton(
            modifier = Modifier
                .align(alignment)
                .size(50.dp),
            onClick = {
                onClick()
            },
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = if (isToggled) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                contentDescription = "Recording control",
                tint = if (isToggled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            )
        }
    }
}