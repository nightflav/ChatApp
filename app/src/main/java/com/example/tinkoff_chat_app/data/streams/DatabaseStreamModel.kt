package com.example.tinkoff_chat_app.data.streams

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tinkoff_chat_app.utils.LocalData.UNSUBSCRIBED_STREAM_TABLE_NAME

@Entity(tableName = UNSUBSCRIBED_STREAM_TABLE_NAME)
data class DatabaseStreamModel(
    @PrimaryKey
    val id: String,
    val name: String,
    val isSelected: Boolean,
)
