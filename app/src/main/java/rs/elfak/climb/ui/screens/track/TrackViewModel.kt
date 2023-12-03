package rs.elfak.climb.ui.screens.track

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.elfak.climb.core.Constants
import rs.elfak.climb.data.model.Response
import rs.elfak.climb.data.model.Track
import rs.elfak.climb.data.model.User
import rs.elfak.climb.data.repository.AuthRepository
import rs.elfak.climb.data.repository.GetTrackResponse
import rs.elfak.climb.data.repository.GetUserResponse
import rs.elfak.climb.data.repository.TrackRepository
import rs.elfak.climb.data.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val trackRepository: TrackRepository
): ViewModel() {
    private val creator = MutableStateFlow<User?>(null)
    private val track = MutableStateFlow<Track?>(null)
    private val userLikeState = MutableStateFlow<Int?>(null)
    private val ratingState = MutableStateFlow<Int>(0)
    private val visited = MutableStateFlow<Int?>(null)
    private val numberOfVisitors = MutableStateFlow<Int>(0)
    fun getCreator(): StateFlow<User?> = creator.asStateFlow()
    fun getTrack(): StateFlow<Track?> = track.asStateFlow()
    fun getLikeState(): StateFlow<Int?> = userLikeState.asStateFlow()
    fun getRatingState(): StateFlow<Int> = ratingState.asStateFlow()
    fun getVisited(): StateFlow<Int?> = visited.asStateFlow()
    fun numberOfVisitors(): StateFlow<Int> = numberOfVisitors.asStateFlow()
    fun getAuthUserId() = authRepository.currentUser?.uid
    var fetchingUserResponse by mutableStateOf<GetUserResponse>(Response.Idle)
        private set
    var fetchingTrackResponse by mutableStateOf<GetTrackResponse>(Response.Idle)
        private set
    fun fetchTrackAndUser(trackId: String) {
        viewModelScope.launch {
            fetchingTrackResponse = Response.Loading
            fetchingTrackResponse = trackRepository.getTrackById(trackId)

            val trackResponse = fetchingTrackResponse
            if (trackResponse is Response.Success) {
                setTrack(trackResponse.data)

                fetchingUserResponse = Response.Idle
                fetchingUserResponse = userRepository.getUserById(trackResponse.data.userId)

                val userResponse = fetchingUserResponse
                if (userResponse is Response.Success) {
                    Log.d(Constants.TAG, "update!")
                    creator.update {
                        userResponse.data
                    }
                }
            }
        }
    }

    fun setTrack(track: Track) {
        this.track.update {
            track
        }
        setUserLikeState(track)
        setInitialRating(track)
        setIsVisited(track)
        setVisitorsCount(track)
    }

    fun toggleLike(trackId: String, newValue: Int?) {
        Log.d(Constants.TAG, "update like")

        val existingTrack = track
        trackRepository.changeLikeState(trackId, newValue, { userId ->
            val newTrack = toggleAndCopy(existingTrack.value!!, userId, newValue)
            track.update {
                newTrack
            }
            userLikeState.update {
                newValue
            }
            setInitialRating(newTrack)
        }, {
            Log.d(Constants.TAG, "FAILURE LIKE")
        })
    }

    private fun toggleAndCopy(track: Track, userId: String, newValue: Int?): Track {
        val newTrack = Track(track)
        val mutableMap = track.userLikes.toMutableMap()

        if (newValue == null) {
            mutableMap.remove(userId)
        } else {
            mutableMap[userId] = newValue
        }

        newTrack.userLikes = mutableMap
        return newTrack
    }

    private fun setUserLikeState(track: Track) {
        val userId = authRepository.currentUser?.uid

        userLikeState.update {
            track.userLikes.get(userId)
        }
    }
    private fun setIsVisited(track: Track) {
        val userId = authRepository.currentUser?.uid

        visited.update {
            track.visitors[userId]
        }
    }

    private fun setVisitorsCount(track: Track) {
        numberOfVisitors.update {
            track.visitors.count()
        }
    }

    private fun setInitialRating(track: Track) {
        ratingState.update {
            track.userLikes.values.fold(0) { acc, cur ->
                acc + cur
            }
        }
    }
}