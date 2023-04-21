package com.example.tinkoff_chat_app.domain.repository.contacts_repository

import com.example.tinkoff_chat_app.models.UserProfile
import com.example.tinkoff_chat_app.network.ChatApi
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi
) : ContactsRepository {

    private var contacts: List<UserProfile>? = null

    private suspend fun loadContacts() {
        val resultMembers = mutableListOf<UserProfile>()
        val users = chatApi.getAllUsers().body()?.members ?: return
        val userPresence = chatApi.getAllUsersPresence().body()?.presences ?: return
        for (member in users) {
            if (member.isBot == false && member.isActive) {
                val currUserPresence = userPresence[member.email]
                resultMembers.add(
                    UserProfile(
                        fullName = member.fullName,
                        status = currUserPresence?.website?.status,
                        avatarSource = member.avatarUrl
                            ?: "https://www.freeiconspng.com/thumbs/no-image-icon/no-image-icon-6.png",
                        email = member.email,
                        id = member.userId
                    )
                )
            }
        }
        contacts = resultMembers
    }

    override suspend fun getAllContacts() = if (contacts == null) {
        loadContacts()
        contacts!!
    } else {
        contacts!!
    }

    override suspend fun getContactsWithSearchRequest(request: String) = (if (contacts == null) {
        loadContacts()
        contacts!!
    } else {
        contacts!!
    }).filter {
        it.fullName.contains(request) ||
                it.email.contains(request) ||
                (it.email + " " + it.fullName).contains(request) ||
                (it.fullName + " " + it.email).contains(request)
    }
}