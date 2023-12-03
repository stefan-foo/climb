package rs.elfak.climb.ui.screens.map

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import rs.elfak.climb.data.repository.TrackRepository
import javax.inject.Inject
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.elfak.climb.core.Constants
import rs.elfak.climb.core.Constants.TAG
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.enums.PathCategory
import rs.elfak.climb.data.enums.RecordingState
import rs.elfak.climb.data.model.Track
import rs.elfak.climb.data.model.Response
import rs.elfak.climb.data.model.User
import rs.elfak.climb.data.repository.GetPathsResponse
import rs.elfak.climb.data.repository.PathCreationResponse
import rs.elfak.climb.data.repository.UserRepository

data class MapUiState(
    val pathPoints: List<LatLng> = listOf(),
    val startTimestamp: Long = 0,
    val endTimestamp: Long = 0,
    val pathDistance: Float = 0F,
    val followingTrack: Track? = Track()
)
data class TrackFilters(
    val radiusKms: Int = DFLT_RADIUS_KMS,
    val trackLengthRangeMeters: ClosedFloatingPointRange<Float> = DFLT_TRACK_LENGTH_RANGE,
    val addedBy: User? = DFLT_ADDED_BY,
    val trackCategories: List<PathCategory> = DFLT_TRACK_CATEGORIES
) {
    companion object {
        const val DFLT_RADIUS_KMS: Int = 100
        val DFLT_TRACK_LENGTH_RANGE: ClosedFloatingPointRange<Float> = 0F..10000F
        val DFLT_ADDED_BY: User? = null
        val DFLT_TRACK_CATEGORIES: List<PathCategory>  = PathCategory.values().toList()
    }
}

@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackRepository: TrackRepository,
    private val userRepository: UserRepository,
): ViewModel() {
    private val _state = MutableStateFlow(MapUiState())
    private val _trackFilters = MutableStateFlow(TrackFilters())
    private val _trackingState = MutableStateFlow(RecordingState.NOT_STARTED)
    private val _updatesStarted = MutableStateFlow(false)
    private val _locationLiveData = LocationLiveData(context)
    private val _lastTrackingRequestLocation = MutableStateFlow<LatLng?>(null)
    private val _relevantPaths = MutableStateFlow<List<Track>>(listOf())
    private var _nearbyTrack = MutableStateFlow<Track?>(null)
    private val _finishedTrack = MutableStateFlow(false)
    fun getFinishedTrack() = _finishedTrack
    fun getNearbyTrack() = _nearbyTrack
    fun setNearbyTrack(value: Track?) {
        this._nearbyTrack.value = value
    }

    var shareLocation = MutableStateFlow(false)
    val usersDataFlow = MutableStateFlow<List<User>>(listOf())
    val filteredPaths = MutableStateFlow<List<Track>>(listOf())
    var pathCreationResponse by mutableStateOf<PathCreationResponse>(Response.Idle)
        private set
    var filterPathsResponse by mutableStateOf<GetPathsResponse>(Response.Success(listOf()))
        private set
    fun getTrackingState() = _trackingState
    fun getLocationLiveData() = _locationLiveData

    fun initialFetchDone() = _lastTrackingRequestLocation.value != null
    init {
        viewModelScope.launch {
            userRepository.getUsers().collect {
                usersDataFlow.value = it
            }
        }
    }
    val state: StateFlow<MapUiState> get() = _state
    fun startLocationUpdates() {
        _locationLiveData.startLocationUpdates()
        _updatesStarted.value = true;

        Log.d(TAG, "starting location updates")
    }
    fun startTracking() {
        if (_locationLiveData.hasObservers()) {
            _trackingState.value = RecordingState.TRACKING
            _state.update {
                it.copy(
                    startTimestamp = System.currentTimeMillis()
                )
            }
        }
    }

    fun fetchOnLocationChange() {
        val currentLocation = _locationLiveData.value
        val lastFetchedLocation = _lastTrackingRequestLocation.value

        if (currentLocation != null && lastFetchedLocation != null) {
            if (Utils.calculateDistance(currentLocation, lastFetchedLocation) > 20) {
                reloadTracksWithSameFilter()
            }
        }
    }

    fun stopTracking() {
        val mutableList = _state.value.pathPoints.toMutableList()
        val endPoint = _locationLiveData.value

        if (endPoint != null) {
            mutableList.add(endPoint)

            if (mutableList.isEmpty()) {
                _state.update {
                    it.copy(
                        pathPoints = mutableList,
                        endTimestamp = System.currentTimeMillis()
                    )
                }
            }

            val distanceFromLastPoint = Utils.calculateDistance(mutableList.last(), endPoint)

            _state.update {
                it.copy(
                    pathPoints = mutableList,
                    endTimestamp = System.currentTimeMillis(),
                    pathDistance = it.pathDistance + distanceFromLastPoint
                )
            }
        }

        _trackingState.value = RecordingState.FINISHED
    }

    fun resetTracking() {
        Log.d(Constants.TAG, "tracking reset")
        _trackingState.value = RecordingState.NOT_STARTED
        _finishedTrack.value = false
        _state.value = MapUiState()
    }
    fun addPoint(point: LatLng) {
        if (_trackingState.value == RecordingState.TRACKING || _trackingState.value == RecordingState.FOLLOWING_TRACK) {
            val currentPath = _state.value.pathPoints
            val mutableList = currentPath.toMutableList()

            if (currentPath.isEmpty()) {
                mutableList.add(point)
                _state.update {
                    it.copy(
                        pathPoints = mutableList
                    )
                }
            } else {
                val lastPoint = mutableList.last()
                val distanceFromLastPoint = Utils.calculateDistance(lastPoint, point)

                if (distanceFromLastPoint > 5) {
                    mutableList.add(point)
                    _state.update {
                        it.copy(
                            pathPoints = mutableList,
                            pathDistance = it.pathDistance + distanceFromLastPoint
                        )
                    }
                }
            }
        }

        if (_trackingState.value == RecordingState.FOLLOWING_TRACK) {
            val endPoint = _nearbyTrack.value?.trackPoints?.last()
            if (endPoint != null) {
                val distance = Utils.calculateDistance(point, endPoint)
                if (distance < 30) {
                    _finishedTrack.value = true
                }
            }
        }
    }

    fun endTracking() {
        val walkedTrack = _nearbyTrack.value

        if (_finishedTrack.value && walkedTrack != null) {
            trackRepository.coveredTrack(walkedTrack)
        }

        resetTracking()
    }
    fun followTrack(track: Track) {
        if (_trackingState.value == RecordingState.TRACKING) {
            resetTracking()
        }

        _trackingState.value = RecordingState.FOLLOWING_TRACK
        _state.update {
            it.copy(
                followingTrack = track
            )
        }
    }
    fun saveTrackWithDetails(
        pathName: String = "",
        pathDifficulty: Int,
        pathCategory: PathCategory,
        bitmap: Bitmap?
    ) {
        val path: Track = Track(
            trackName = pathName,
            trackLengthMeters = _state.value.pathDistance,
            trackPoints = _state.value.pathPoints,
            category = pathCategory,
            difficulty = pathDifficulty,
            durationSeconds = _state.value.endTimestamp.minus(_state.value.startTimestamp).div(1000),
            startingPoint = _state.value.pathPoints[0],
        )

        viewModelScope.launch {
            pathCreationResponse = Response.Loading
            pathCreationResponse = trackRepository.createTrack(path, bitmap)

            reloadTracksWithSameFilter()
        }
    }

    fun reloadTracksWithSameFilter() {
        viewModelScope.launch {
            val currentLocation = _locationLiveData.value ?: LatLng(0.0, 0.0)

            filterPathsResponse = Response.Loading
            filterPathsResponse = trackRepository.getTracksByRadius(
                center = currentLocation,
                radiusMeters = _trackFilters.value.radiusKms * 1000,
            )

            _lastTrackingRequestLocation.value = currentLocation

            val snapshot = filterPathsResponse
            if (snapshot is Response.Success) {
                _relevantPaths.value = snapshot.data
                reloadFilteredPaths()
            }
        }
    }
    fun filterTracks(
        radiusKms: Int,
        trackLengthRangeMeters: ClosedFloatingPointRange<Float> = 0F..10000F,
        trackCategories: List<PathCategory> = PathCategory.values().toList(),
        addedBy: User? = null
    ) {
        _trackFilters.value = TrackFilters(
            radiusKms = radiusKms,
            trackLengthRangeMeters = trackLengthRangeMeters,
            trackCategories = trackCategories,
            addedBy = addedBy
        )

        val currentLocation = _locationLiveData.value ?: LatLng(0.0, 0.0)
        _lastTrackingRequestLocation.value = currentLocation

        viewModelScope.launch {
            filterPathsResponse = Response.Loading
            filterPathsResponse = trackRepository.getTracksByRadius(
                center = currentLocation,
                radiusMeters = (radiusKms) * 1000,
            )

            val snapshot = filterPathsResponse
            if (snapshot is Response.Success) {
                _relevantPaths.value = snapshot.data
                reloadFilteredPaths()
            }
        }
    }

    private fun reloadFilteredPaths() {
        filteredPaths.value = _relevantPaths.value.filter {
            (it.trackLengthMeters >= _trackFilters.value.trackLengthRangeMeters.start) &&
            (it.trackLengthMeters <= _trackFilters.value.trackLengthRangeMeters.endInclusive) &&
            (_trackFilters.value.trackCategories.isEmpty() || _trackFilters.value.trackCategories.contains(it.category)) &&
            (_trackFilters.value.addedBy == null || _trackFilters.value.addedBy?.userId == it.userId) &&
            (Utils.calculateDistance(it.startingPoint, _locationLiveData.value ?: LatLng(0.0, 0.0)) < _trackFilters.value.radiusKms * 1000)
        }
    }
    
    fun toggleShareLocation() {
        this.shareLocation.value = !shareLocation.value
    }
    fun updateUserLocation(location: LatLng) {
        userRepository.writeCurrentLocation(location)
    }
    fun fetchNewTracksByRadius(
        center: LatLng,
        radiusMeters: Int,
    ) {
        viewModelScope.launch {
            filterPathsResponse = Response.Loading
            filterPathsResponse = trackRepository.getTracksByRadius(
                center = center,
                radiusMeters = radiusMeters,
            )

            _lastTrackingRequestLocation.value = center

            val snapshot = filterPathsResponse
            if (snapshot is Response.Success) {
                _relevantPaths.value = snapshot.data
                reloadFilteredPaths()
            }
        }
    }
    fun resetRequestState() {
        pathCreationResponse = Response.Idle
    }
    companion object {
        private const val CACHED_RADIUS_M = 50
    }
}