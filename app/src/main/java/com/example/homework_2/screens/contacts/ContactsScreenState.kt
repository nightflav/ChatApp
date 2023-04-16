package com.example.homework_2.screens.contacts

import com.example.homework_2.models.UserProfile

sealed class ContactsScreenState {
    data class Error(val e: Exception) : ContactsScreenState()
    object Loading : ContactsScreenState()
    object Init : ContactsScreenState()
    class Success(val profiles: List<UserProfile>) : ContactsScreenState()
}