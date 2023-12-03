package rs.elfak.climb.ui.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import rs.elfak.climb.data.model.User
import rs.elfak.climb.data.repository.UserRepository
import javax.inject.Inject
import android.util.Log
import rs.elfak.climb.core.Constants

@HiltViewModel
class LeaderboardModelView @Inject constructor(
    private val userRepo: UserRepository
): ViewModel() {
    private val _usersDataFlow = MutableStateFlow<List<User>>(listOf())
    fun getUsersData() = _usersDataFlow
    init {
        viewModelScope.launch {
            userRepo.getUsers().collect {
                Log.d(Constants.TAG, "Collecting leaderboard")

                _usersDataFlow.value = it.reversed()

                Log.d(Constants.TAG, it.size.toString())
            }
        }
    }
}