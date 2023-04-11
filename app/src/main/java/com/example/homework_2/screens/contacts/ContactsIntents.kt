package com.example.homework_2.screens.contacts

sealed class ContactsIntents {
    object InitContacts : ContactsIntents()
    data class SearchForUserIntent(val request: String) : ContactsIntents()
}
