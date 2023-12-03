package rs.elfak.climb.data.repository.impl

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import rs.elfak.climb.data.model.Response
import rs.elfak.climb.data.model.User
import rs.elfak.climb.data.repository.FirestoreCollections
import rs.elfak.climb.data.repository.UpdateUserDataResponse
import rs.elfak.climb.data.repository.UserRepository
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import androidx.core.net.toUri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import rs.elfak.climb.data.model.LocationData
import rs.elfak.climb.data.repository.DBCollections
import rs.elfak.climb.data.repository.GetUserResponse
import rs.elfak.climb.data.repository.StorageCollections
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val realtimeDb: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
): UserRepository {
    override suspend fun updateUserData(
        username: String,
        fullName: String?,
        phone: String?,
        image: Bitmap?): UpdateUserDataResponse {

        val userId = auth.currentUser?.uid
            ?: return Response.Failure(IllegalStateException("User not authenticated"))

        var profileImageUrl: String = ""
        if (image != null) {
            val storageReference = storage.reference
                .child(StorageCollections.PROFILE_IMAGES)
                .child(userId)

            val byteArrayOutputStream = ByteArrayOutputStream()
            image?.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
            val imageData = byteArrayOutputStream.toByteArray()
            val uploadTask = storageReference.putBytes(imageData)

            uploadTask.await()

            if (!uploadTask.isSuccessful) {
                return Response.Failure(Exception("Error occurred while uploading image"))
            }

            profileImageUrl = storageReference.downloadUrl.await().toString()
        }

        return try {
            val map: MutableMap<String, Any> = mutableMapOf()
            map["phone"] = phone.toString()
            if (username.isNotBlank())
                map["username"] = username
            map["image"] = profileImageUrl
            map["fullName"] = fullName.toString()

            realtimeDb.getReference(FirestoreCollections.USERS)
                .child(userId)
                .updateChildren(map)
                .await()

            auth.currentUser?.updateProfile(userProfileChangeRequest {
                photoUri = profileImageUrl.toUri()
                displayName = username
            })?.await()

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getUsers(): Flow<List<User>> = callbackFlow {
        val usersRef = realtimeDb.getReference(DBCollections.USERS).orderByChild("distanceCovered")

        val firebaseDataListeners = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.children

                val users = value.mapNotNull { it.getValue(User::class.java) }

                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserRepositoryImpl", error.message);
            }
        }

        usersRef.addValueEventListener(firebaseDataListeners)

        awaitClose {
            usersRef.removeEventListener(firebaseDataListeners)
        }
    }
    override fun writeCurrentLocation(
        location: LatLng,
        successCallback: () -> Unit,
        failureCallback: () -> Unit
    ) {
        val map: MutableMap<String, Any> = mutableMapOf()
        map["lastKnownLocation"] = LocationData(location)
        map["lastActiveTimestamp"] = System.currentTimeMillis()

        realtimeDb.getReference(FirestoreCollections.USERS)
            .child(auth.uid.toString())
            .updateChildren(map)
            .addOnSuccessListener {
                successCallback()
            }
            .addOnFailureListener {
                failureCallback()
            }
    }

    override suspend fun getUserById(userId: String): GetUserResponse {
        try {
            val userDoc = realtimeDb
                .getReference("${FirestoreCollections.USERS}/${userId}")
                .get().await()


            val userObj = userDoc.getValue(User::class.java)
            userObj?.let {
                return Response.Success(userObj)
            }

            return Response.Failure(Exception("Error occured while fetching user"))
        } catch (e: Exception) {
            return Response.Failure(e)
        }
    }
}