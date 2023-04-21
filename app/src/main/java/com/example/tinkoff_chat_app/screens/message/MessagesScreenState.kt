package com.example.tinkoff_chat_app.screens.message

import com.example.tinkoff_chat_app.models.SingleMessage

sealed class MessagesScreenState {
    data class Error(val error: Exception) : MessagesScreenState()
    object Init : MessagesScreenState()
    object Loading : MessagesScreenState()
    class Success(val messages: List<SingleMessage>) : MessagesScreenState()
}
