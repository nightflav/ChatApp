package com.example.homework_2.datasource

import com.example.homework_2.models.UserProfile
import com.example.homework_2.network.RetrofitInstance.Companion.chatApi
import com.example.homework_2.network.networkModels.users.Member
import kotlinx.coroutines.delay
import retrofit2.Response

object ProfilesDatasource {

    private var contacts: List<UserProfile> = emptyList()
    private var profile: Response<Member>? = null

    suspend fun getProfile(): Response<Member> {
        if(profile == null)
            profile = chatApi.getProfile()
        return profile!!
    }

    suspend fun getContacts(): List<UserProfile> {
        val resultMembers = mutableListOf<UserProfile>()
        val users = chatApi.getAllUsers().body()?.members ?: return emptyList()
        for (member in users) {
            if (member.is_bot == false && member.is_active) {
                val currUserPresence = chatApi.getUserPresence(member.email).body()
                resultMembers.add(
                    UserProfile(
                        fullName = member.full_name,
                        status = currUserPresence!!.presence.aggregated.status,
                        avatarSource = member.avatar_url
                            ?: "https://www.freeiconspng.com/thumbs/no-image-icon/no-image-icon-6.png",
                        email = member.email
                    )
                )
            }
        }
        contacts = resultMembers
        return contacts
    }

    suspend fun getContactsWithSearchRequest(request: String): List<UserProfile> {
        delay(500L)
        return contacts.filter {
            it.fullName.contains(request) ||
                    it.email.contains(request) ||
                    (it.email + " " + it.fullName).contains(request) ||
                    (it.fullName + " " + it.email).contains(request)
        }
    }
}