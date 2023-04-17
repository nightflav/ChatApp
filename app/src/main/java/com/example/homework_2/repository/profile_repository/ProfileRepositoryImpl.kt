package com.example.homework_2.repository.profile_repository

import com.example.homework_2.models.UserProfile
import com.example.homework_2.network.RetrofitInstance
import com.example.homework_2.screens.profile.ProfileScreenState
import com.example.homework_2.utils.toUserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfileRepositoryImpl : ProfileRepository {

    companion object {
        private var profile: UserProfile? = null
    }

    suspend fun getProfile(): UserProfile {
        if (profile == null)
            profile = RetrofitInstance.chatApi.getProfile().toUserProfile()
        return profile!!
    }

    override suspend fun getProfileState(): Flow<ProfileScreenState> = flow {
        emit(ProfileScreenState.Loading)
        if(profile == null) {
            try {
                getProfile()
                emit(ProfileScreenState.Success(profile!!))
            } catch (e: java.lang.Exception) {
                emit(ProfileScreenState.Error)
            }
        } else {
            emit(ProfileScreenState.Success(profile!!))
        }
    }

}