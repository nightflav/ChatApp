package com.example.homework_2.screens.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_2.domain.repository.contacts_repository.ContactsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactsViewModel @Inject constructor(
    private val repo: ContactsRepository
) : ViewModel() {

    val contactsChannel = Channel<ContactsIntents>()
    private val _screenState: MutableStateFlow<ContactsScreenState> =
        MutableStateFlow(ContactsScreenState.Init)
    val screenState get() = _screenState.asStateFlow()

    init {
        subscribeForIntents()
    }

    private fun subscribeForIntents() {
        viewModelScope.launch {
            contactsChannel.consumeAsFlow().collect {
                when(it) {
                    is ContactsIntents.SearchForUserIntent -> searchForContact(it.request)
                    is ContactsIntents.InitContacts -> loadContacts()
                }
            }
        }
    }

    private suspend fun loadContacts() {
        repo.getAllContacts().collect {
            _screenState.emit(it)
        }
    }

    private suspend fun searchForContact(request: String) {
        repo.getContactsWithSearchRequest(request).collect {
            _screenState.emit(it)
        }
    }
}