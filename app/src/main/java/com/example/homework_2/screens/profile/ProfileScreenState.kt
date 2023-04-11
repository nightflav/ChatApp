package com.example.homework_2.screens.profile

import com.example.homework_2.models.UserProfile

sealed class ProfileScreenState {
    object Loading : ProfileScreenState()
    object Error : ProfileScreenState()
    object Init : ProfileScreenState()
    data class Success(val profile: UserProfile) : ProfileScreenState()
}
