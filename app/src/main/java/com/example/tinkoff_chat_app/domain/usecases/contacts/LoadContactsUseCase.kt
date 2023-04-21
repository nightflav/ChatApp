package com.example.tinkoff_chat_app.domain.usecases.contacts

import com.example.tinkoff_chat_app.domain.repository.contacts_repository.ContactsRepository
import com.example.tinkoff_chat_app.models.UserProfile
import javax.inject.Inject

class LoadContactsUseCase @Inject constructor(
    private val contactsRepo: ContactsRepository
) {
    suspend operator fun invoke(): List<UserProfile> = contactsRepo.getAllContacts()
}