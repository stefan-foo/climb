package rs.elfak.climb.ui.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.enums.RecordingState
import rs.elfak.climb.ui.screens.map.components.CreateTrack
import rs.elfak.climb.ui.screens.map.components.CreateTrackDialog
import rs.elfak.climb.ui.screens.map.components.RecordFAB
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.sharp.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.MapUiSettings
import rs.elfak.climb.components.UserMarker
import rs.elfak.climb.core.Constants
import rs.elfak.climb.ui.screens.map.components.BottomSheet
import rs.elfak.climb.ui.screens.map.components.FollowTrackFAB
import rs.elfak.climb.ui.screens.map.components.TrackListDialog
import rs.elfak.climb.ui.screens.map.components.TrackMarker
import rs.elfak.climb.ui.screens.profile.ProfileViewModel

@OptIn(
    ExperimentalPermissionsApi::class,
    MapsComposeExperimentalApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    navigateToTrackScreen: (trackId: String) -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) {
        viewModel.startLocationUpdates()
    }

    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val recordingState by viewModel.getTrackingState().collectAsStateWithLifecycle()
    val currentLocation = viewModel.getLocationLiveData().observeAsState(initial = null)
    val relevantPaths by viewModel.filteredPaths.collectAsStateWithLifecycle()
    val users by viewModel.usersDataFlow.collectAsStateWithLifecycle()
    val sharingLocation by viewModel.shareLocation.collectAsStateWithLifecycle()
    val nearbyTrack by viewModel.getNearbyTrack().collectAsStateWithLifecycle()
    val finishedTrack by viewModel.getFinishedTrack().collectAsStateWithLifecycle()

    val isRecordingActive = remember { derivedStateOf { recordingState == RecordingState.TRACKING }}
    val openTableView = rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        locationPermissionState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(currentLocation.value, relevantPaths) {
        if (recordingState != RecordingState.FOLLOWING_TRACK) {
            val track = Utils.trackInRadius(currentLocation.value ?: LatLng(0.0, 0.0), relevantPaths, 30)
            viewModel.setNearbyTrack(track)
        }
    }

    LaunchedEffect(Unit) {
        Log.d("DEBUG", recordingState.name)
        viewModel.getLocationLiveData().observe(
            lifecycleOwner
        ) { current ->
            if (recordingState == RecordingState.TRACKING || recordingState == RecordingState.FOLLOWING_TRACK) {
                if (current != null) {
                    viewModel.addPoint(current)
                }
            }

            viewModel.fetchOnLocationChange()

            if (current != null && !viewModel.initialFetchDone()) {
                Log.d(Constants.TAG, "initial fetch")

                viewModel.filterTracks(
                    addedBy = TrackFilters.DFLT_ADDED_BY,
                    radiusKms = TrackFilters.DFLT_RADIUS_KMS,
                    trackCategories = TrackFilters.DFLT_TRACK_CATEGORIES,
                    trackLengthRangeMeters = TrackFilters.DFLT_TRACK_LENGTH_RANGE
                )
            }

            if (sharingLocation) {
                viewModel.updateUserLocation(current)
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState()

    val mapProperties = MapProperties(
        isMyLocationEnabled = currentLocation.value != null
    )

    if (openTableView.value) {
        TrackListDialog(
            location = currentLocation.value,
            tracks = relevantPaths,
            onDismiss = {
                openTableView.value = false
            },
            navigateToTrack = navigateToTrackScreen
        )
    }

    Scaffold(
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.TopCenter
            ) {
                BottomSheetScaffold(
                    sheetTonalElevation = 0.dp,
                    scaffoldState = scaffoldState,
                    sheetContent = { BottomSheet(
                        users = users,
                        onFilter = { radiusKms, trackLengthRange, categories, addedBy ->
                            viewModel.filterTracks(
                                addedBy = addedBy,
                                radiusKms = radiusKms,
                                trackCategories = categories,
                                trackLengthRangeMeters = trackLengthRange
                            )
                        }
                    )},
                    sheetPeekHeight = 30.dp
                ) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 0.dp)) {
                        GoogleMap(
                            properties = mapProperties,
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            contentPadding = PaddingValues(bottom = 20.dp),
                            uiSettings = MapUiSettings(myLocationButtonEnabled = false)
                        ) {
                            MapEffect() {
                                it.setOnCameraIdleListener {
                                    scope.launch {
                                        val currentLoc = currentLocation.value
                                        if (recordingState == RecordingState.TRACKING && currentLoc != null) {
                                            cameraPositionState.centerOnLocation(currentLoc, 17F)
                                        }
                                    }
                                }
                            }

                            if (recordingState == RecordingState.TRACKING) {
                                Polyline(
                                    points = viewState.pathPoints,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            if (recordingState == RecordingState.FOLLOWING_TRACK) {
                                nearbyTrack?.let {
                                    Polyline(
                                        points = it.trackPoints,
                                        color = MaterialTheme.colorScheme.secondary,
                                        zIndex = 2F,
                                        endCap = RoundCap(),
                                    )
                                }

                                Polyline(
                                    points = viewState.pathPoints,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                relevantPaths.forEach {
                                    Polyline(
                                        points = it.trackPoints,
                                        color = MaterialTheme.colorScheme.primary,
                                        zIndex = 1F,
                                        endCap = RoundCap()
                                    )
                                }
                            }

                            if (sharingLocation) {
                                users.forEach {
                                    if (profileViewModel.getCurrentUser()?.uid != it.userId
                                        && System.currentTimeMillis().minus(it.lastActiveTimestamp) > 60000 * 5) {
                                        UserMarker(
                                            user = it
                                        )
                                    }
                                }
                            }

                            relevantPaths.forEach {
                                TrackMarker(
                                    track = it,
                                    onClick = {
                                        navigateToTrackScreen(it.id)
                                    },
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 125.dp, end = 12.dp)
                        ) {
                            SmallFloatingActionButton(
                                onClick = {
                                    scope.launch {
                                        val currentLoc = currentLocation.value
                                        if (currentLoc != null) {
                                            cameraPositionState.centerOnLocation(currentLoc)
                                        }
                                    }
                                },
                                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.BottomEnd),
                                containerColor = Color.White.copy(alpha = 0.5F),
                                content = {
                                    Icon(
                                        modifier = Modifier.size(25.dp),
                                        imageVector = Icons.Sharp.MyLocation,
                                        contentDescription = "Location",
                                        tint = Color.DarkGray
                                    )
                                }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 175.dp, end = 12.dp)
                        ) {
                            SmallFloatingActionButton(
                                onClick = {
                                    viewModel.reloadTracksWithSameFilter()
                                },
                                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.BottomEnd),
                                containerColor = Color.White.copy(alpha = 0.5F),
                                content = {
                                    Icon(
                                        modifier = Modifier.size(25.dp),
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = "Location",
                                        tint = Color.DarkGray
                                    )
                                }
                            )
                        }

                        CreateTrack(onSuccess = { viewModel.resetTracking() })

                        if (recordingState == RecordingState.FINISHED) {
                            CreateTrackDialog(
                                pathDistance = viewState.pathDistance,
                                durationMilliseconds = viewState
                                    .endTimestamp.minus(viewState.startTimestamp).div(1000)
                                    .toDuration(DurationUnit.SECONDS),
                                onClose = { viewModel.resetTracking() },
                            ) { a, b, c, d -> viewModel.saveTrackWithDetails(a, b, c, d) }
                        }

                        RecordFAB(
                            isToggled = recordingState == RecordingState.TRACKING,
                            alignment = Alignment.BottomStart,
                            modifier = Modifier.padding(start = 18.dp, bottom = 90.dp),
                            onClick = {
                            if (!isRecordingActive.value) {
                                viewModel.startTracking()
                            } else {
                                viewModel.stopTracking()
                            }
                        })
                        
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 18.dp, bottom = 40.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            RichTooltipBox(
                                title = { Text(Constants.SHARE_LOCATION_TITLE) },
                                text = { Text(Constants.SHARE_LOCATION_TEXT)}
                            ) {
                                Switch(
                                    modifier = Modifier.width(35.dp).padding(start = 15.dp),
                                    checked = sharingLocation,
                                    onCheckedChange = { viewModel.toggleShareLocation() }
                                )
                            }

                            if (relevantPaths.isNotEmpty()) {
                                FloatingActionButton(
                                    content = {
                                        Icon(
                                            modifier = Modifier.size(35.dp),
                                            contentDescription = null,
                                            imageVector = Icons.Rounded.Explore,
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(bottom = 110.dp)
                                        .size(50.dp),
                                    onClick = {
                                        openTableView.value = !openTableView.value
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                )
                            }

                            nearbyTrack?.let {
                                FollowTrackFAB(
                                    recordingState = recordingState,
                                    isFinished = finishedTrack,
                                    pulse = recordingState != RecordingState.FOLLOWING_TRACK || finishedTrack,
                                    onClick = {
                                        if (recordingState != RecordingState.FOLLOWING_TRACK) {
                                            nearbyTrack?.let {
                                                viewModel.followTrack(it)
                                            }
                                        } else {
                                            viewModel.endTracking()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
    )
}
private suspend fun CameraPositionState.centerOnLocation(
    location: LatLng, zoom: Float = 13F
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        location,
        zoom
    ),
)
