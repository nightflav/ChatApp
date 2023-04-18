package com.example.homework_2.repository.contacts_repository

import android.util.Log
import com.example.homework_2.models.UserProfile
import com.example.homework_2.network.ChatApi
import com.example.homework_2.screens.contacts.ContactsScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override suspend fun getAllContacts(): Flow<ContactsScreenState> {
        return flow {
            emit(ContactsScreenState.Loading)
            if (contacts == null) {
                try {
                    Log.d("msgmsgmsg", "start to load contatcs")
                    loadContacts()
                    Log.d("msgmsgmsg", "contacts loaded")
                    emit(ContactsScreenState.Success(contacts!!))
                } catch (e: Exception) {
                    emit(ContactsScreenState.Error(e))
                }
            } else {
                emit(ContactsScreenState.Success(contacts!!))
            }
        }
    }

    override suspend fun getContactsWithSearchRequest(request: String): Flow<ContactsScreenState> =
        flow {
            emit(ContactsScreenState.Loading)

            if (contacts != null)
                emit(ContactsScreenState.Success(contacts!!.filter {
                    it.fullName.contains(request) ||
                            it.email.contains(request) ||
                            (it.email + " " + it.fullName).contains(request) ||
                            (it.fullName + " " + it.email).contains(request)
                }))
            else
                emit(ContactsScreenState.Error(Exception()))
        }
}