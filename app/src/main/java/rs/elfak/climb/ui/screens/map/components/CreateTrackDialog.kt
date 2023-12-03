package rs.elfak.climb.ui.screens.map.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.elfak.climb.R
import rs.elfak.climb.components.Select
import rs.elfak.climb.components.Spacer8
import rs.elfak.climb.data.enums.PathCategory
import rs.elfak.climb.ui.screens.personalize.components.ImageSelector
import kotlin.time.Duration

@Composable
fun CreateTrackDialog(
    pathDistance: Float,
    durationMilliseconds: Duration,
    onClose: () -> Unit,
    onSave: (
        pathName: String,
        pathDifficulty: Int,
        pathCategory: PathCategory,
        bitmap: Bitmap?
    ) -> Unit,
) {
    val hours: Long = durationMilliseconds.inWholeHours
    val minutes = durationMilliseconds.inWholeMinutes - (60 * hours)
    val seconds = durationMilliseconds.inWholeSeconds - (3600 * hours) - minutes * 60
    val formattedDuration = "$hours H : $minutes M : $seconds S"

    val pathName = remember { mutableStateOf("") }
    val difficulty = remember { mutableStateOf(3) }
    val category = remember { mutableStateOf(PathCategory.WALKING) }
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = {
            onClose()
        },
        title = {
            Text(text = "Save track")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Distance: ${pathDistance.toInt()} meters", fontSize = 18.sp)
                Text("Duration: $formattedDuration", fontSize = 18.sp)

                ImageSelector(
                    onImageChange = { bitmap.value = it },
                    image = bitmap.value,
                    placeholder = {
                        Image(
                            painter = painterResource(R.drawable.mountainplaceholder),
                            contentDescription = null,
                            modifier = Modifier.size(160.dp).alpha(0.4F)
                        )
                    },
                    modifier = Modifier.size(160.dp)
                )
                
                OutlinedTextField(
                    value = pathName.value,
                    onValueChange = { pathName.value = it },
                    label = {
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer8()
                
                Select(
                    options = (1..5).toList(),
                    value = difficulty.value,
                    onSelectionChanged = { difficulty.value = it },
                    display = { value -> value.toString() },
                    label = "Terrain difficulty",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer8()

                Select(
                    options = PathCategory.values().toList(),
                    value = category.value,
                    onSelectionChanged = { category.value = it },
                    display = { p -> p.category },
                    label = "Category",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(pathName.value, difficulty.value, category.value, bitmap.value)
                }) {
                Text("Save path")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onClose()
                }) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun preview() {
    Box(modifier = Modifier.fillMaxSize()) {
        CreateTrackDialog(
            pathDistance = 0F,
            durationMilliseconds = Duration.ZERO,
            onClose = {  },
            onSave = { a, b, c, e -> true }
        )
    }
}