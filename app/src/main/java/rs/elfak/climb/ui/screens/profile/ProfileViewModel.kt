package rs.elfak.climb.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.elfak.climb.data.model.Response
import rs.elfak.climb.data.repository.AuthRepository
import rs.elfak.climb.data.repository.ReloadUserResponse
import rs.elfak.climb.data.repository.RevokeAccessResponse
import javax.inject.Inject
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import rs.elfak.climb.data.model.User
import rs.elfak.climb.data.repository.GetUserResponse
import rs.elfak.climb.data.repository.UserRepository

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val userRepo: UserRepository
): ViewModel() {
    private val _user = MutableStateFlow<User?>(null)

    var revokeAccessResponse by mutableStateOf<RevokeAccessResponse>(Response.Success(false))
        private set
    var reloadUserResponse by mutableStateOf<ReloadUserResponse>(Response.Success(false))
        private set
    var userLoadedResponse by mutableStateOf<GetUserResponse>(Response.Idle)
        private set

    init {
        viewModelScope.launch {
            val userId = repo.currentUser?.uid
            userId?.let {
                userLoadedResponse = Response.Loading
                userLoadedResponse = userRepo.getUserById(it)

                val userResponse = userLoadedResponse
                if (userResponse is Response.Success) {
                    _user.value = userResponse.data
                }
            }
        }
        Log.d("ProfileViewModel", "created")
    }

    fun getUser() = _user
    fun getCurrentUser() = repo.currentUser
    fun reloadUser() = viewModelScope.launch {
        reloadUserResponse = Response.Loading
        reloadUserResponse = repo.reloadFirebaseUser()
    }

    val isEmailVerified get() = repo.currentUser?.isEmailVerified ?: false

    fun signOut() = repo.signOut()

    fun revokeAccess() = viewModelScope.launch {
        revokeAccessResponse = Response.Loading
        revokeAccessResponse = repo.revokeAccess()
    }
}