package com.example.homework_2.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_2.repository.profile_repository.ProfileRepositoryImpl
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    val profileChannel = Channel<ProfileIntents>()
    private val _screenState: MutableStateFlow<ProfileScreenState> =
        MutableStateFlow(ProfileScreenState.Init)
    val screenState get() = _screenState.asStateFlow()
    private val repo = ProfileRepositoryImpl()

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