package com.example.tinkoff_chat_app.models.db_models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.TopicModel
import com.example.tinkoff_chat_app.utils.LocalData.STREAM_TABLE_NAME

@Entity(tableName = STREAM_TABLE_NAME)
data class DatabaseStreamModel(
    @PrimaryKey
    val id: Int,
    val name: String,
    val isSelected: Boolean,
    val isSubscriptionStream: Boolean = false,
    val topics: List<TopicModel>?
)
