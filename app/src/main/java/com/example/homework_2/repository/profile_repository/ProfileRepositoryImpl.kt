package com.example.homework_2.repository.profile_repository

import com.example.homework_2.models.UserProfile
import com.example.homework_2.network.ChatApi
import com.example.homework_2.network.network_models.users.Member
import com.example.homework_2.screens.profile.ProfileScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi
) : ProfileRepository {

    private var profile: UserProfile? = null

    suspend fun getProfile(): UserProfile {
        if (profile == null)
            profile = chatApi.getProfile().toUserProfile()
        return profile!!
    }

    override suspend fun getProfileState(): Flow<ProfileScreenState> = flow {
        emit(ProfileScreenState.Loading)
        if (profile == null) {
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

    private suspend fun Response<Member>.toUserProfile(): UserProfile? {
        val member = body()
        return if (this.isSuccessful && member != null) {
            val currUserPresence = chatApi.getUserPresence(member.email).body()
            UserProfile(
                fullName = member.fullName,
                status = currUserPresence!!.presence.aggregated.status,
                avatarSource = member.avatarUrl
                    ?: "https://www.freeiconspng.com/thumbs/no-image-icon/no-image-icon-6.png",
                email = member.email,
                id = member.userId
            )
        } else null
    }
}