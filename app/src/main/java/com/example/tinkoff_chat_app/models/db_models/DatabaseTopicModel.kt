package com.example.tinkoff_chat_app.models.db_models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tinkoff_chat_app.utils.LocalData.TOPIC_TABLE_NAME

@Entity(tableName = TOPIC_TABLE_NAME)
data class DatabaseTopicModel(
    @PrimaryKey
    val id: String,
    val name: String,
    var msgCount: Int,
    val parentId: Int,
    val parentName: String
)
