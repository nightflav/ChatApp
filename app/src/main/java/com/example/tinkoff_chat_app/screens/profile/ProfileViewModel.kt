package com.example.tinkoff_chat_app.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinkoff_chat_app.domain.repository.profile_repository.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository
) : ViewModel() {

    val profileChannel = Channel<ProfileIntents>()
    private val _screenState: MutableStateFlow<ProfileScreenState> =
        MutableStateFlow(ProfileScreenState.Init)
    val screenState get() = _screenState.asStateFlow()

    init {
        subscribeForIntents()
    }

    private fun subscribeForIntents() {
        viewModelScope.launch {
            profileChannel.consumeAsFlow().collect {
                when(it) {
                    is ProfileIntents.InitProfile -> loadProfile()
                }
            }
        }
    }

    private suspend fun loadProfile() {
        repo.getProfileState().collect {
            _screenState.emit(it)
        }
    }

}