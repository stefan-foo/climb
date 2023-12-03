package rs.elfak.climb.ui.screens.personalize.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import rs.elfak.climb.components.Spacer8
import rs.elfak.climb.core.Constants

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PersonalizeContent(
    padding: PaddingValues,
    updateUser: (username: String, fullName: String?, phone: String?, bitmap: Bitmap?) -> Unit
) {
    val bitmap = rememberSaveable {
        mutableStateOf<Bitmap?>(null)
    }
    val phone = rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val username = rememberSaveable {
        mutableStateOf<String>("")
    }
    val fullName = rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val focusRequester = FocusRequester()
    val keyboard = LocalSoftwareKeyboardController.current

    fun onImageChange(image: Bitmap?) {
        bitmap.value = image
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState(), enabled = true)
        ) {
            ImageSelector(
                onImageChange = { onImageChange(it) },
                image = bitmap.value,
                modifier = Modifier.size(260.dp)
            )

            Spacer8()

            OutlinedTextField(
                value = username.value ?: "",
                onValueChange = { username.value = it },
                label = {
                    Text(
                        text = Constants.USERNAME_LABEL
                    )
                },
                singleLine = true,
                modifier = Modifier.focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer8()

            OutlinedTextField(
                value = phone.value ?: "",
                onValueChange = { phone.value = it },
                label = {
                    Text(
                        text = Constants.PHONE_LABEL
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer8()

            OutlinedTextField(
                value = fullName.value ?: "",
                onValueChange = { fullName.value = it },
                label = {
                    Text(
                        text = Constants.FULL_NAME_LABEL
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer8()

            Button(
                onClick = {
                    keyboard?.hide()
                    updateUser(username.value, fullName.value, phone.value, bitmap.value)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Finish")
            }
        }
    }

}

@Preview
@Composable
fun PersonalizeContentPreview() {
    PersonalizeContent(updateUser = { a,b,c,d -> true}, padding = PaddingValues(8.dp))
}