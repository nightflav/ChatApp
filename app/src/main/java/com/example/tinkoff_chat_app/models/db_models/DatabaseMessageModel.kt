package com.example.tinkoff_chat_app.models.db_models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tinkoff_chat_app.models.data_transfer_models.ReactionDto
import com.example.tinkoff_chat_app.utils.LocalData.MESSAGE_TABLE_NAME

@Entity(tableName = MESSAGE_TABLE_NAME)
data class DatabaseMessageModel(
    @PrimaryKey
    val id: Int,
    val senderName: String,
    val senderId: Int,
    val msg: String,
    var reactions: List<ReactionDto>,
    val date: Int,
    val topicName: String,
    val streamName: String,
    val avatarUri: String
)
