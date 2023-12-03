package rs.elfak.climb.data.model

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import rs.elfak.climb.data.enums.PathCategory
import java.util.Date

class Track(
    val trackPoints: List<LatLng> = listOf(),
    val trackLengthMeters: Float = 0f,
    val durationSeconds: Long = 0,
    val trackName: String = "",
    val difficulty: Int = 0,
    val startingPoint: LatLng = LatLng(0.0, 0.0),
    val category: PathCategory = PathCategory.WALKING,
    val userId: String = "",
    var id: String = "",
    val imageUri: String = "",
    val rating: Int = 0,
    var createdAt: Date = Date(),
    var userLikes: Map<String, Int> = emptyMap(),
    var visitors: Map<String, Int> = emptyMap()
) {
    constructor(path: DbTrack) : this(
        trackPoints = path.trackPoints.map { LatLng(it.latitude, it.longitude) },
        userId = path.userId,
        trackName = path.trackName,
        startingPoint = LatLng(path.startingPoint.latitude, path.startingPoint.longitude),
        trackLengthMeters = path.trackLengthMeters,
        durationSeconds = path.durationSeconds,
        category = path.category,
        difficulty = path.difficulty,
        imageUri = path.imageUri,
        rating = path.rating,
        createdAt = path.createdAt,
        userLikes = path.userLikes,
        visitors = path.visitors
    )

    constructor(path: Track) : this(
        trackPoints = path.trackPoints,
        userId = path.userId,
        trackName = path.trackName,
        startingPoint = path.startingPoint,
        trackLengthMeters = path.trackLengthMeters,
        durationSeconds = path.durationSeconds,
        category = path.category,
        difficulty = path.difficulty,
        imageUri = path.imageUri,
        rating = path.rating,
        createdAt = path.createdAt,
        userLikes = path.userLikes,
        id = path.id,
        visitors = path.visitors
    )
}

class DbTrack(
    val trackPoints: List<LocationData> = listOf(),
    val trackLengthMeters: Float = 0F,
    val durationSeconds: Long = 0,
    var trackName: String = "",
    val difficulty: Int = 0,
    val startingPoint: LocationData = LocationData(),
    val category: PathCategory = PathCategory.WALKING,
    val userId: String = "",
    val geohash: String = "",
    val imageUri: String = "",
    val rating: Int = 0,
    val createdAt: Date = Date(),
    var userLikes: Map<String, Int> = emptyMap(),
    var visitors: Map<String, Int> = emptyMap()
) {
    constructor(userId: String, path: Track) : this(
        userId = userId,
        startingPoint = LocationData(path.startingPoint),
        difficulty = path.difficulty,
        durationSeconds = path.durationSeconds,
        trackPoints = path.trackPoints.map { LocationData(it) },
        category = path.category,
        geohash = GeoFireUtils.getGeoHashForLocation(
            GeoLocation(
                path.startingPoint.latitude,
                path.startingPoint.longitude
            )
        ),
        trackLengthMeters = path.trackLengthMeters,
        trackName = path.trackName,
        rating = path.rating,
        imageUri = path.imageUri,
        userLikes = path.userLikes,
        visitors = path.visitors
    )
}

class LocationData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    constructor(point: LatLng) : this(point.latitude, point.longitude)
}