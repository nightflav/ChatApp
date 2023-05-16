package com.example.tinkoff_chat_app.domain.repository.profile_repository

import com.example.tinkoff_chat_app.models.ui_models.UserProfile
import com.example.tinkoff_chat_app.screens.profile.ProfileScreenState
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getProfile(): UserProfile

    suspend fun getProfileState(): Flow<ProfileScreenState>

}