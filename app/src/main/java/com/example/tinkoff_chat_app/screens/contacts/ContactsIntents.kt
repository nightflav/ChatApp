package com.example.tinkoff_chat_app.screens.contacts

sealed class ContactsIntents {
    object InitContacts : ContactsIntents()
    data class SearchForUserIntent(val request: String) : ContactsIntents()
}
