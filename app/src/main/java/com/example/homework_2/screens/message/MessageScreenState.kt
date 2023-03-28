package com.example.homework_2.screens.message

import com.example.homework_2.models.SingleMessage

sealed class MessageScreenState {
    object Error : MessageScreenState()
    object Init : MessageScreenState()
    object Loading : MessageScreenState()
    class Data(val messages: List<SingleMessage>) : MessageScreenState()
}
