package com.example.tinkoff_chat_app.screens.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinkoff_chat_app.domain.usecases.contacts.LoadContactsUseCase
import com.example.tinkoff_chat_app.domain.usecases.contacts.SearchForContactUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactsViewModel @Inject constructor(
    private val loadContactsUseCase: LoadContactsUseCase,
    private val searchForContactUseCase: SearchForContactUseCase
) : ViewModel() {

    val contactsChannel = Channel<ContactsIntents>()
    private val _screenState: MutableStateFlow<ContactsScreenUiState> =
        MutableStateFlow(ContactsScreenUiState())
    val screenState get() = _screenState.asStateFlow()
    private val currState
        get() = screenState.value

    init {
        subscribeForIntents()
    }

    private fun subscribeForIntents() {
        viewModelScope.launch {
            contactsChannel.consumeAsFlow().collect {
                when (it) {
                    is ContactsIntents.SearchForUserIntent -> searchForContact(it.request)
                    is ContactsIntents.InitContacts -> loadContacts()
                }
            }
        }
    }

    private suspend fun loadContacts() {
        _screenState.emit(
            currState.copy(
                isLoading = true
            )
        )
        _screenState.emit(
            try {
                currState.copy(
                    contacts = loadContactsUseCase(),
                    isLoading = false
                )
            } catch (e: Exception) {
                currState.copy(
                    error = e,
                    isLoading = false
                )
            }
        )
    }

    private suspend fun searchForContact(request: String) {
        _screenState.emit(
            try {
                currState.copy(
                    contacts = searchForContactUseCase(request),
                    isLoading = false
                )
            } catch (e: Exception) {
                currState.copy(
                    error = e,
                    isLoading = false
                )
            }
        )
    }
}