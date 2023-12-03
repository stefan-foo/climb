package rs.elfak.climb.core

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rs.elfak.climb.data.enums.RecordingState
import rs.elfak.climb.data.model.Track
import rs.elfak.climb.data.model.User
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.time.Duration.Companion.seconds

class Utils {
    companion object {
        fun print(e: Exception) = Log.e(Constants.TAG, e.stackTraceToString())

        fun showMessage(
            context: Context,
            message: String?
        ) = makeText(context, message, LENGTH_LONG).show()

        fun calculateDistance(a: LatLng, b: LatLng): Float {
            val results = FloatArray(1)
            Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, results)
            return results[0]
        }
        fun calculatePathLength(pathPoints: List<LatLng>): Float {
            if (pathPoints.size <= 1)
                return 0F

            var totalLength = 0F
            for (i in 0 until pathPoints.size - 1) {
                val startPoint = pathPoints[i]
                val endPoint = pathPoints[i + 1]
                totalLength += calculateDistance(startPoint, endPoint)
            }

            return totalLength
        }

        fun filterUsers(users: List<User>, query: String, limit: Int = 10): List<User> {
            val filteredUsers: MutableList<User> = mutableListOf()
            val pattern = Regex(".*${Regex.escape(query)}.*", RegexOption.IGNORE_CASE)

            for (user in users) {
                if (pattern.containsMatchIn(user.email) ||
                    pattern.containsMatchIn(user.username)) {
                    filteredUsers.add(user)
                }
            }

            return filteredUsers;
        }

        fun getDefaultUsername(email: String): String? {
            val sections = email.split("@")

            return if (sections.isEmpty() || sections[0].isEmpty()) {
                null;
            } else {
                sections[0]
            }
        }

        fun formatDate(date: Date, format: String): String {
            val formatter = SimpleDateFormat(format)
            return formatter.format(date)
        }

        fun formatTimeFromSeconds(seconds: Long): String {
            if (seconds < 60) return seconds.toString()
            return DateUtils.formatElapsedTime(seconds)
        }

        fun <T> debounce(
            waitMs: Long = 300L,
            coroutineScope: CoroutineScope,
            destinationFunction: (T) -> Unit
        ): (T) -> Unit {
            var debounceJob: Job? = null
            return { param: T ->
                debounceJob?.cancel()
                debounceJob = coroutineScope.launch {
                    delay(waitMs)
                    destinationFunction(param)
                }
            }
        }

        fun formatDistance(distanceMeters: Long): String {
            return if (distanceMeters < 10000) {
                "$distanceMeters m"
            } else {
                String.format("%.2f km", distanceMeters.toFloat() / 1000)
            }
        }
        fun checkIfLiesInside(prevLocation: LatLng, prevRadiusKm: Int, currentLocation: LatLng, curRadiusKm: Int): Boolean {
            if (curRadiusKm >= prevRadiusKm) return false;
            val distanceBetweenCentersKms = Utils.calculateDistance(prevLocation, currentLocation) / 1000
            return (distanceBetweenCentersKms + curRadiusKm) < prevRadiusKm
        }

        private fun isInRadius(center: LatLng, point: LatLng, radiusMeters: Int): Boolean {
            if (calculateDistance(center, point) <= radiusMeters) {
                return true;
            }
            return false;
        }
        fun trackInRadius(center: LatLng, tracks: List<Track>, radiusMeters: Int): Track? {
            for (track in tracks) {
                if (isInRadius(center, track.startingPoint, radiusMeters)) {
                    return track
                }
            }
            return null
        }

        fun getTint(recordingState: RecordingState, finishedTrack: Boolean): Color {
            return if (recordingState == RecordingState.FOLLOWING_TRACK && finishedTrack) {
                Color.Green
            } else if (recordingState == RecordingState.FOLLOWING_TRACK) {
                Color.Yellow
            } else {
                Color.Blue
            }
        }
    }
}