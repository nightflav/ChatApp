package com.example.tinkoff_chat_app.data.messages

import androidx.room.*
import com.example.tinkoff_chat_app.models.db_models.DatabaseMessageModel
import com.example.tinkoff_chat_app.utils.LocalData.MESSAGE_TABLE_NAME

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<DatabaseMessageModel>)

    @Query(
        "SELECT * FROM $MESSAGE_TABLE_NAME" +
                " WHERE topicName = :fromTopic" +
                " AND streamName = :fromStream"
    )
    suspend fun getMessages(fromTopic: String, fromStream: String): List<DatabaseMessageModel>

    @Delete
    suspend fun deleteMessage(message: DatabaseMessageModel)

    @Query(
        "DELETE FROM $MESSAGE_TABLE_NAME" +
                " WHERE topicName = :fromTopic" +
                " AND streamName = :fromStream"
    )
    suspend fun deleteMessagesOfCurrentTopic(fromTopic: String, fromStream: String)

    @Query(
        "DELETE FROM $MESSAGE_TABLE_NAME" +
                " WHERE streamName = :fromStream"
    )
    suspend fun deleteMessagesOfCurrentStream(fromStream: String)

    @Query(
        "SELECT * FROM $MESSAGE_TABLE_NAME" +
                " WHERE streamName = :fromStream"
    )
    suspend fun getMessagesFromStream(fromStream: String): List<DatabaseMessageModel>

}