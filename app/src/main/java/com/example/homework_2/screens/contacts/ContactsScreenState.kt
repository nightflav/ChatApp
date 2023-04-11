package com.example.homework_2.screens.contacts

import com.example.homework_2.models.UserProfile

sealed class ContactsScreenState {
    object Error : ContactsScreenState()
    object Loading : ContactsScreenState()
    object Init : ContactsScreenState()
    class Success(val profiles: List<UserProfile>) : ContactsScreenState()
}