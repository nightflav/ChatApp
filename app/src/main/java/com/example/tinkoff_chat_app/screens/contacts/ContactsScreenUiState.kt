package com.example.tinkoff_chat_app.screens.contacts

import com.example.tinkoff_chat_app.models.ui_models.UserProfile

data class ContactsScreenUiState(
    val contacts: List<UserProfile> = emptyList(),
    val error: Exception? = null,
    val isLoading: Boolean = true,
    val request: String = ""
)
