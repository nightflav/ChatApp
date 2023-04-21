package com.example.tinkoff_chat_app.screens.profile

import com.example.tinkoff_chat_app.models.UserProfile

sealed class ProfileScreenState {
    object Loading : ProfileScreenState()
    data class Error(val error: Exception) : ProfileScreenState()
    object Init : ProfileScreenState()
    data class Success(val profile: UserProfile) : ProfileScreenState()
}
