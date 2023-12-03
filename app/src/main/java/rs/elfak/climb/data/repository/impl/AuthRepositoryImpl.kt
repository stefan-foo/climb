package rs.elfak.climb.data.repository.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import rs.elfak.climb.data.repository.*
import rs.elfak.climb.data.model.Response.Success
import rs.elfak.climb.data.model.Response.Failure;
import rs.elfak.climb.data.model.User
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log;
import com.google.firebase.auth.ktx.userProfileChangeRequest
import rs.elfak.climb.core.Utils

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val realtimeDb: FirebaseDatabase
) : AuthRepository {
    private val tag = "AuthRepositoryImpl";
    override val currentUser get() = auth.currentUser

    override suspend fun firebaseSignUpWithEmailAndPassword(
        email: String, password: String
    ): SignUpResponse {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            if (authResult.user?.uid != null) {
                val defaultUsername = Utils.getDefaultUsername(email).toString()

                val user = User(
                    email = authResult.user?.email.toString(),
                    image = "",
                    phone = "",
                    username = defaultUsername,
                    userId = authResult.user?.uid.toString()
                )

                realtimeDb.getReference(DBCollections.USERS).child(authResult.user!!.uid)
                    .setValue(user)
                    .await()

                auth.currentUser?.updateProfile(userProfileChangeRequest {
                    displayName = defaultUsername
                })?.await()
            }

            Success(true)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun sendEmailVerification(): SendEmailVerificationResponse {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Success(true)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun firebaseSignInWithEmailAndPassword(
        email: String, password: String
    ): SignInResponse {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Success(true)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun reloadFirebaseUser(): ReloadUserResponse {
        return try {
            auth.currentUser?.reload()?.await()
            Success(true)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): SendPasswordResetEmailResponse {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Success(true)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun signOut() = auth.signOut()

    override suspend fun revokeAccess(): RevokeAccessResponse {
        return try {
            auth.currentUser?.delete()?.await()
            Success(true)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun getAuthState(viewModelScope: CoroutineScope) = callbackFlow {
        val authStateListener = AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), auth.currentUser == null)
}