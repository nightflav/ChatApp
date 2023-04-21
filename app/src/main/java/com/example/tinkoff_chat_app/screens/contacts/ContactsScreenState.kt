package com.example.tinkoff_chat_app.screens.contacts

import com.example.tinkoff_chat_app.models.UserProfile

sealed class ContactsScreenState {
    data class Error(val e: Exception) : ContactsScreenState()
    object Loading : ContactsScreenState()
    object Init : ContactsScreenState()
    class Success(val profiles: List<UserProfile>) : ContactsScreenState()
}