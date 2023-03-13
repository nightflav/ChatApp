package com.example.homework_2.models

data class SingleMessage(
    val senderName: String = "",
    val msg: String = "",
    var reactions: MutableList<Reaction> = mutableListOf(),
    val user_id: String = "dataSeparator",
    val message_id: String = "",
    val isDataSeparator: Boolean = false,
    val date: String = "01 Feb"
)