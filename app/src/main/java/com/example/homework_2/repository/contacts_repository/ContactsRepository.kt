package com.example.homework_2.repository.contacts_repository

import com.example.homework_2.screens.contacts.ContactsScreenState
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    suspend fun getAllContacts(): Flow<ContactsScreenState>

    suspend fun getContactsWithSearchRequest(request: String): Flow<ContactsScreenState>
}