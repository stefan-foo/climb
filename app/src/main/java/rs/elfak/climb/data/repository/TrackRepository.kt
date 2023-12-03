package rs.elfak.climb.data.repository

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import rs.elfak.climb.data.model.Track
import rs.elfak.climb.data.model.Response

typealias PathCreationResponse = Response<Boolean>
typealias GetPathsResponse = Response<List<Track>>
typealias GetTrackResponse = Response<Track>
typealias ChangeLikeStateResponse = Response<Boolean>
interface TrackRepository {
    suspend fun createTrack(path: Track, bitmap: Bitmap?): PathCreationResponse;
    suspend fun getTracksByRadius(
        center: LatLng,
        radiusMeters: Int,
    ): GetPathsResponse

    suspend fun getTrackById(uid: String): GetTrackResponse
    fun coveredTrack(track: Track, onSuccess: () -> Unit = {}, onFailure: () -> Unit = {});

    fun changeLikeState(
        trackId: String,
        nextState: Int?,
        onSuccess: (userId: String) -> Unit,
        onFailure: (msg: String) -> Unit
    )
}