package com.example.tinkoff_chat_app.domain.repository.profile_repository

import com.example.tinkoff_chat_app.screens.profile.ProfileScreenState
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getProfileState(): Flow<ProfileScreenState>

}