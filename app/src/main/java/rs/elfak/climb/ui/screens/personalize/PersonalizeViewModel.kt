package rs.elfak.climb.ui.screens.personalize

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.elfak.climb.data.model.Response
import rs.elfak.climb.data.repository.UpdateUserDataResponse
import rs.elfak.climb.data.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class PersonalizeViewModel @Inject constructor(
    private val userRepo: UserRepository
): ViewModel()
{
    var updateUserResponse by mutableStateOf<UpdateUserDataResponse>(Response.Idle)
        private set

    fun updateUser(username: String, fullName: String?, phone: String?, bitmap: Bitmap?) = viewModelScope.launch {
        updateUserResponse = Response.Loading
        updateUserResponse = userRepo.updateUserData(username, fullName, phone, bitmap)
    }
}