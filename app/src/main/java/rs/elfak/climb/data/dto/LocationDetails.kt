package rs.elfak.climb.data.dto

import com.google.android.gms.maps.model.LatLng
import rs.elfak.climb.data.enums.PathCategory

data class PathDetails (
    val startingPoint: LatLng,
    val path: List<LatLng>,
    val name: String?,
    val difficulty: Int,
    val category: PathCategory,
    val pathLengthMeters: Int,
    val durationToCrossMinutes: Int
)