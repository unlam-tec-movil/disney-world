package dev.leotoloza.avengersapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.customSignals
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class EventsUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PanelControlViewModel @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState

    private val _isRemoteConfigButtonEnabled = MutableStateFlow(false)
    val isRemoteConfigButtonEnabled: StateFlow<Boolean> = _isRemoteConfigButtonEnabled.asStateFlow()
    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 1000
    }
    val customSignals = customSignals {
        put("OS", "ANDROID")
        put("pais", "AR")
    }

    init {
        fetchRemoteConfig()
        listenForUpdates()
    }

    private fun fetchRemoteConfig() {
        remoteConfig.setCustomSignals(customSignals)
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    updateButtonState()
                } else {
                    Log.e(TAG, "Fetch failed")
                }
            }
    }

    private fun listenForUpdates() {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                if (configUpdate.updatedKeys.contains(FEATURE_BUTTON_ENABLED_KEY)) {
                    remoteConfig.activate().addOnCompleteListener {
                        updateButtonState()
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })
    }

    private fun updateButtonState() {
        _isRemoteConfigButtonEnabled.value = remoteConfig.getBoolean(FEATURE_BUTTON_ENABLED_KEY)
    }

    fun onForceCrash() {
        throw RuntimeException("Crash forzado desde el Panel de Control")
    }

    fun onErrorShown() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun onLogout() {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
    }

    companion object {
        private const val TAG = "PanelControlViewModel"
        private const val FEATURE_BUTTON_ENABLED_KEY = "feature_button_enabled"
    }
}
