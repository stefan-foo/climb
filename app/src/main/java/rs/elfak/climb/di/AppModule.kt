package rs.elfak.climb.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import rs.elfak.climb.data.repository.AuthRepository
import rs.elfak.climb.data.repository.TrackRepository
import rs.elfak.climb.data.repository.UserRepository
import rs.elfak.climb.data.repository.impl.AuthRepositoryImpl
import rs.elfak.climb.data.repository.impl.TrackRepositoryImpl
import rs.elfak.climb.data.repository.impl.UserRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {
    @Provides
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl(
        auth = Firebase.auth,
        realtimeDb = FirebaseDatabase.getInstance()
    )

    @Provides
    fun provideUserRepository(): UserRepository = UserRepositoryImpl(
        auth = Firebase.auth,
        realtimeDb = FirebaseDatabase.getInstance(),
        storage = FirebaseStorage.getInstance()
    )

    @Provides
    fun providePathRepository(): TrackRepository = TrackRepositoryImpl(
        auth = Firebase.auth,
        firestore = FirebaseFirestore.getInstance(),
        realtimeDb = FirebaseDatabase.getInstance(),
        storage = FirebaseStorage.getInstance()
    )
}