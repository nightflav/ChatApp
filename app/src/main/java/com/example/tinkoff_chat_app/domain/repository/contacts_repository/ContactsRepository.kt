package com.example.tinkoff_chat_app.domain.repository.contacts_repository

import com.example.tinkoff_chat_app.models.ui_models.UserProfile

interface ContactsRepository {
    suspend fun getAllContacts(): List<UserProfile>

    suspend fun getContactsWithSearchRequest(request: String): List<UserProfile>
}