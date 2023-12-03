package rs.elfak.climb.data.repository.impl

import android.graphics.Bitmap
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import rs.elfak.climb.core.Utils
import rs.elfak.climb.data.model.DbTrack
import rs.elfak.climb.data.model.Track
import rs.elfak.climb.data.model.Response
import rs.elfak.climb.data.repository.FirestoreCollections
import rs.elfak.climb.data.repository.GetPathsResponse
import rs.elfak.climb.data.repository.PathCreationResponse
import rs.elfak.climb.data.repository.TrackRepository
import javax.inject.Inject
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import rs.elfak.climb.core.Constants
import rs.elfak.climb.data.repository.DBCollections
import rs.elfak.climb.data.repository.GetTrackResponse
import rs.elfak.climb.data.repository.StorageCollections
import java.io.ByteArrayOutputStream

class TrackRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val realtimeDb: FirebaseDatabase,
    private val storage: FirebaseStorage
): TrackRepository {
    override suspend fun createTrack(path: Track, bitmap: Bitmap?): PathCreationResponse {
        return try {
            val userId = auth.currentUser?.uid ?: return Response.Failure(Exception("User not authenticated"))

            val dbPath = DbTrack(userId = userId, path)
            if (dbPath.trackName.isBlank()) {
                dbPath.trackName = "${auth.currentUser?.displayName}'s track"
            }

            val result = firestore.collection(FirestoreCollections.PATHS).add(dbPath).await()

            realtimeDb
                .getReference(FirestoreCollections.USERS)
                .child(userId)
                .child("distanceCovered")
                .setValue(ServerValue.increment(path.trackLengthMeters.toLong()))

            if (bitmap != null) {
                val storageReference = storage.reference
                    .child(StorageCollections.TRACK_IMAGES)
                    .child(result.id)

                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val imageData = byteArrayOutputStream.toByteArray()

                storageReference.putBytes(imageData).addOnSuccessListener {
                    val map = mutableMapOf<String, Any>()
                    storageReference.downloadUrl.addOnSuccessListener {
                        map["imageUri"] = it.toString()
                        firestore
                            .collection(FirestoreCollections.PATHS)
                            .document(result.id)
                            .update(map)
                    }
                }
            }

            Response.Success(true);
        } catch (e: Exception) {
            Log.e(Constants.TAG, e.message.toString())
            Response.Failure(e);
        }
    }

    override suspend fun getTracksByRadius(
        center: LatLng,
        radiusMeters: Int
    ): GetPathsResponse {
        return try {
            val bounds = GeoFireUtils.getGeoHashQueryBounds(GeoLocation(center.latitude, center.longitude), radiusMeters.toDouble())
            val tasks = mutableListOf<Task<QuerySnapshot>>()

            val collectionRef = firestore.collection(FirestoreCollections.PATHS)

            bounds.forEach {
                val specQuery = collectionRef
                    .orderBy("geohash")
                    .startAt(it.startHash)
                    .endAt(it.endHash)

                tasks.add(specQuery.get());
            }

            val deferred = tasks.map { it -> it.asDeferred() }
            val results = deferred.awaitAll()

            val matchingPaths = mutableListOf<Track>()

            results.forEach { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    val path = document.toObject(DbTrack::class.java)

                    if (path?.startingPoint != null) {
                        val startingPoint = LatLng(
                            path.startingPoint.latitude,
                            path.startingPoint.longitude
                        )
                        if (Utils.calculateDistance(center, startingPoint) < radiusMeters) {
                            val track = Track(path)
                            track.id = document.id
                            matchingPaths.add(track)
                        }
                    }
                }
            }

            Log.i("gyros", matchingPaths.size.toString())
            Response.Success(matchingPaths);
        } catch (e: Exception) {
            Log.i("gyros", e.message.toString())
            Response.Failure(e);
        }
    }

    override fun coveredTrack(track: Track, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            onFailure()
            return
        }

        realtimeDb
            .getReference(DBCollections.USERS)
            .child(userId)
            .child("distanceCovered")
            .setValue(ServerValue.increment(track.trackLengthMeters.toLong()))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }

        val updates = hashMapOf<String, Any>("visitors.$userId" to FieldValue.increment(1L))
        firestore
            .collection(FirestoreCollections.PATHS)
            .document(track.id)
            .update(updates)
    }

    override suspend fun getTrackById(uid: String): GetTrackResponse {
        try {
            val ref = firestore.collection(FirestoreCollections.PATHS).document(uid).get().await()
            val dbTrack = ref.toObject(DbTrack::class.java)

            dbTrack?.let {
                val track = Track(dbTrack)
                track.id = ref.id
                return Response.Success(track)
            }

            return Response.Failure(Exception("Error occurred while fetching track"))
        } catch (e: Exception) {
            return Response.Failure(e)
        }
    }

    override fun changeLikeState(trackId: String, nextState: Int?, onSuccess: (userId: String) -> Unit, onFailure: (msg: String) -> Unit) {
        try {
            val userId = auth.currentUser?.uid

            if (userId == null) {
                onFailure("User not authenticated")
                return
            }

            val ref = firestore
                .collection(FirestoreCollections.PATHS)
                .document(trackId)

            Log.e(Constants.TAG, nextState.toString())

            if (nextState == null) {
                val updates = hashMapOf<String, Any>("userLikes.$userId" to FieldValue.delete())
                ref.update(updates)
                    .addOnSuccessListener {
                        onSuccess(userId)
                    }
            } else {
                val updates = hashMapOf<String, Any>("userLikes.$userId" to nextState.toLong())
                ref.update(updates)
                    .addOnSuccessListener {
                        onSuccess(userId)
                    }
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, e.message.toString())
            onFailure(e.message.toString())
        }


    }
}