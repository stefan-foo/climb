package rs.elfak.climb.ui.screens.map.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.estimateAnimationDurationMillis
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RunCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.enums.RecordingState
import kotlin.math.absoluteValue

@Composable
fun FollowTrackFAB(
    recordingState: RecordingState,
    isFinished: Boolean,
    pulse: Boolean,
    onClick: () -> Unit
) {
    var lastScaleValue by remember { mutableStateOf<Float>(1F) }
    val anim = remember(pulse) { Animatable(lastScaleValue) }

    LaunchedEffect(pulse) {
        if (pulse) {
            anim.animateTo(
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse
                )
            ) {
                lastScaleValue = this.value
            }
        } else {
            lastScaleValue = 1F
        }
    }

    FloatingActionButton(
        content = {
            Icon(
                modifier = Modifier.size(35.dp).scale(lastScaleValue),
                contentDescription = null,
                imageVector = Icons.Rounded.RunCircle,
                tint = Utils.getTint(recordingState, isFinished)
            )
        },
        modifier = Modifier
            .padding(bottom = 170.dp)
            .size(50.dp),
        onClick = {
           onClick()
        },
        shape = RoundedCornerShape(12.dp),
    )
}
