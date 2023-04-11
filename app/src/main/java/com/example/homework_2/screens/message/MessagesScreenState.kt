package com.example.homework_2.screens.message

import com.example.homework_2.models.SingleMessage

sealed class MessagesScreenState {
    object Error : MessagesScreenState()
    object Init : MessagesScreenState()
    object Loading : MessagesScreenState()
    class Success(val messages: List<SingleMessage>) : MessagesScreenState()
}
