package rs.elfak.climb.data.model

import com.google.firebase.Timestamp
import java.util.Date

class User (
    val userId: String = "",
    val email: String = "",
    val username: String = "",
    val phone: String = "",
    val image: String = "",
    val fullName: String = "",
    val lastKnownLocation: LocationData? = null,
    val lastActiveTimestamp: Long = 0,
    val distanceCovered: Long = 0,
)