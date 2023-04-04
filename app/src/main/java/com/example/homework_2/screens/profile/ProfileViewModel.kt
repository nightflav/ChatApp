package com.example.homework_2.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_2.datasource.ProfilesDatasource.getProfile
import com.example.homework_2.models.UserProfile
import com.example.homework_2.runCatchingNonCancellation
import com.example.homework_2.toUserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _screenState: MutableStateFlow<ProfileScreenState> =
        MutableStateFlow(ProfileScreenState.Init)
    val screenState get() = _screenState.asStateFlow()

    private var profile: UserProfile? = null

    init {
        viewModelScope.launch {
            _screenState.emit(loadProfile())
        }
    }

    private suspend fun loadProfile(): ProfileScreenState {
        val loadedProfile = runCatchingNonCancellation {
            _screenState.emit(ProfileScreenState.Loading)
            profile = getProfile().toUserProfile()
            profile
        }.onFailure {
            Log.d(
                "StreamViewModelDebugLog",
                "Failed on searching for stream, caused by ${it.cause}"
            )
        }.getOrNull()

        return getScreenState(loadedProfile)
    }

    private fun getScreenState(state: UserProfile?): ProfileScreenState {
        return state?.let { ProfileScreenState.Profile(it) }
            ?: ProfileScreenState.Error
    }

}