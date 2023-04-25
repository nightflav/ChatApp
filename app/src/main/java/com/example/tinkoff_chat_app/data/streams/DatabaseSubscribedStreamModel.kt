package com.example.tinkoff_chat_app.data.streams

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tinkoff_chat_app.utils.LocalData.SUBSCRIBED_STREAM_TABLE_NAME

@Entity(tableName = SUBSCRIBED_STREAM_TABLE_NAME)
data class DatabaseSubscribedStreamModel(
    @PrimaryKey
    val id: String,
    val name: String,
    val isSelected: Boolean,
)
