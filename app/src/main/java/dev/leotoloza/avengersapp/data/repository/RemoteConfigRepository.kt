package dev.leotoloza.avengersapp.data.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface RemoteConfigRepository {
    suspend fun fetchAndActivate(): Boolean
    fun isFirebaseUiEnabled(): Boolean
}

class RemoteConfigRepositoryImpl @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) : RemoteConfigRepository {

    override suspend fun fetchAndActivate(): Boolean {
        return try {
            val result = remoteConfig.fetchAndActivate().await()
            android.util.Log.d("RemoteConfig", "Fetch and activate result: $result")
            result
        } catch (e: Exception) {
            android.util.Log.e("RemoteConfig", "Fetch failed", e)
            false
        }
    }

    override fun isFirebaseUiEnabled(): Boolean {
        return remoteConfig.getBoolean("activar_firebase_auth_ui")
    }
}
