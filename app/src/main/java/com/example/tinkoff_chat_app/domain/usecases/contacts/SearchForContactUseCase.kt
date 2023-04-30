package com.example.tinkoff_chat_app.domain.usecases.contacts

import com.example.tinkoff_chat_app.domain.repository.contacts_repository.ContactsRepository
import com.example.tinkoff_chat_app.models.ui_models.UserProfile
import javax.inject.Inject

class SearchForContactUseCase @Inject constructor(
    private val contactsRepo: ContactsRepository
) {
    suspend operator fun invoke(request: String): List<UserProfile> = contactsRepo.getContactsWithSearchRequest(request)
}
