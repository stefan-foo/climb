package rs.elfak.climb.ui.screens.personalize.components

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rs.elfak.climb.components.ClimbImage
import rs.elfak.climb.components.Spacer8
import kotlin.reflect.KFunction0

@Composable
fun ImageSelector(
    onImageChange: (Bitmap?) -> Unit,
    image: Bitmap?,
    modifier: Modifier = Modifier,
    text: String = "Pick image",
    placeholder: @Composable () -> Unit = {
        Icon(
            modifier = modifier,
            contentDescription = null,
            imageVector = Icons.Rounded.AccountCircle,
            tint = MaterialTheme.colorScheme.primary
        )
    }
) {
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri
        }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        imageUri?.let {
            val source = ImageDecoder
                .createSource(context.contentResolver, it)

            onImageChange(ImageDecoder.decodeBitmap(source))
        }

        if (image != null) {
            val bitmap = image.asImageBitmap()
            ClimbImage(bitmap = bitmap, contentDescription = null, modifier = modifier)
        } else {
            placeholder()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.clickable {
                launcher.launch("image/*")
            }
        ) {
            Icon(
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
                imageVector = Icons.Rounded.AddPhotoAlternate
            )
            Text(text)
        }
    }
}
