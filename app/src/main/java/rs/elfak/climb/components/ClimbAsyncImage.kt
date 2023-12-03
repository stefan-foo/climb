package rs.elfak.climb.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun ClimbAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp,
    resourceId: Int?,
    size: Size? = null,
    placeholderAlt: @Composable () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.background
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(size ?: Size.ORIGINAL)
            .build()
    )

    ClimbSurface(
        elevation = elevation,
        shape = CircleShape,
        modifier = modifier,
        color = backgroundColor
    ) {
        if (painter.state is AsyncImagePainter.State.Success) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        if (painter.state is AsyncImagePainter.State.Loading ||
            painter.state is AsyncImagePainter.State.Empty ||
            painter.state is AsyncImagePainter.State.Error) {

            if (resourceId != null) {
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().alpha(0.4F),
                    contentScale = ContentScale.Crop
                )
            } else {
                placeholderAlt()
            }
        }
    }
}