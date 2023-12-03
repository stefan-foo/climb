package rs.elfak.climb.ui.screens.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.sharp.Email
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material.icons.sharp.Person2
import androidx.compose.material.icons.sharp.Person3
import androidx.compose.material.icons.sharp.Person4
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import rs.elfak.climb.R
import rs.elfak.climb.components.ClimbAsyncImage
import rs.elfak.climb.components.Spacer8
import rs.elfak.climb.core.Constants.WELCOME_MESSAGE
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.model.User

@Composable
fun ProfileContent(
    padding: PaddingValues,
    user: User
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                ProfileHeader(user = user)
                Spacer(modifier = Modifier.height(16.dp))
                UserInfoFields(user)
            }
        }
    }
}

@Composable
private fun ProfileHeader(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
            )
            .background(MaterialTheme.colorScheme.inversePrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClimbAsyncImage(
                imageUrl = user.image,
                modifier = Modifier.size(180.dp),
                contentDescription = null,
                placeholderAlt = {
                    Icon(
                        modifier = Modifier.size(180.dp),
                        contentDescription = null,
                        imageVector = Icons.Rounded.AccountCircle,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                resourceId = null
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user.fullName,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )
            Text(
                text = Utils.formatDistance(user.distanceCovered),
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
@Composable
private fun UserInfoFields(user: User) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(start = 32.dp, end = 32.dp)) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(text = user.username, fontSize = 16.sp)
        }

        Spacer8()
        Divider(color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(text = user.email, fontSize = 16.sp)
        }

        Spacer8()
        Divider(color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(text = user.phone, fontSize = 16.sp)
        }

        Spacer8()
        Divider(color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(16.dp))
    }
}



@Preview
@Composable
fun userProfile(){
    ProfileContent(padding = PaddingValues(0.dp), user = User(
        username = "gyros",
        fullName = "Stefan Stojadinovic",
        distanceCovered = 24000,
        email = "gyros@gmail.com",
        image = "https://firebasestorage.googleapis.com/v0/b/climb-a42b4.appspot.com/o/profile_images%2FSqqxnvhOFGOBmoWxdTw0SVBYZEO2?alt=media&token=0d7cfb36-6471-4bdb-9a90-659cddf24bc2"
    ))
}