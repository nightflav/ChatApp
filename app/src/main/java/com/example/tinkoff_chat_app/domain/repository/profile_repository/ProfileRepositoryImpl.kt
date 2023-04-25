package com.example.tinkoff_chat_app.domain.repository.profile_repository

import android.content.SharedPreferences
import com.example.tinkoff_chat_app.models.UserProfile
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.network.network_models.users.Member
import com.example.tinkoff_chat_app.screens.profile.ProfileScreenState
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_FULLNAME
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_ID
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_STATUS
import com.example.tinkoff_chat_app.utils.Network.MISSING_AVATAR_URL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val spProfile: SharedPreferences
) : ProfileRepository {

    private var profile: UserProfile? = null

    suspend fun getProfile(): UserProfile {
        if (profile == null) {
            profile = chatApi.getProfile().toUserProfile()
            val fullName = profile!!.fullName
            val id = profile!!.id
            val status = profile!!.status
            with(spProfile.edit()) {
                putString(SP_PROFILE_FULLNAME, fullName)
                putInt(SP_PROFILE_ID, id)
                putString(SP_PROFILE_STATUS, status)
            }
        }
        return profile!!
    }

    override suspend fun getProfileState(): Flow<ProfileScreenState> = flow {
        emit(ProfileScreenState.Loading)
        if (profile == null) {
            try {
                getProfile()
                emit(ProfileScreenState.Success(profile!!))
            } catch (e: Exception) {
                emit(ProfileScreenState.Error(e))
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
                    ?: MISSING_AVATAR_URL,
                email = member.email,
                id = member.userId
            )
        } else null
    }
}