package com.example.tinkoff_chat_app.models.ui_models

data class MessageModel(
    val avatarUri: String,
    val senderName: String = "",
    val msg: String = "",
    var reactions: MutableList<MessageReaction> = mutableListOf(),
    val user_id: String = "dataSeparator",
    val message_id: String = "",
    val isDataSeparator: Boolean = false,
    val date: String = "01 Feb"
)