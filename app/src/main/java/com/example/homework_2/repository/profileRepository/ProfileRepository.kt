package com.example.homework_2.repository.profileRepository

import com.example.homework_2.screens.profile.ProfileScreenState
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getProfileState(): Flow<ProfileScreenState>

}