package rs.elfak.climb.ui.screens.sign_in

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.elfak.climb.data.model.Response
import rs.elfak.climb.data.repository.AuthRepository
import rs.elfak.climb.data.repository.SignInResponse
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject
    constructor(private val authRepo: AuthRepository): ViewModel() {
    var signInResponse by mutableStateOf<SignInResponse>(Response.Idle)
        private set

    fun signInWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        signInResponse = Response.Loading
        signInResponse = authRepo.firebaseSignInWithEmailAndPassword(email, password)
    }
}