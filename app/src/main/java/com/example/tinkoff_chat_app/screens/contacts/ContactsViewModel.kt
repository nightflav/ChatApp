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
        _screenState.emit(try {
            ContactsScreenState.Success(loadContactsUseCase())
        } catch (e: Exception) {
            ContactsScreenState.Error(e)
        })
    }

    private suspend fun searchForContact(request: String) {
        _screenState.emit(try {
            ContactsScreenState.Success(searchForContactUseCase(request))
        } catch (e: Exception) {
            ContactsScreenState.Error(e)
        })
    }
}