package com.example.tinkoff_chat_app.data.messages

import androidx.room.*
import com.example.tinkoff_chat_app.utils.LocalData.MESSAGE_TABLE_NAME

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMessage(message: DatabaseMessageModel)

    @Query(value = "SELECT * FROM $MESSAGE_TABLE_NAME" +
            " WHERE topicName = :fromTopic" +
            " AND streamName = :fromStream")
    suspend fun readAllMessages(fromTopic: String, fromStream: String): List<DatabaseMessageModel>

    @Query("SELECT COUNT(id) FROM $MESSAGE_TABLE_NAME")
    suspend fun getItemCount(): Int

    @Query("DELETE FROM $MESSAGE_TABLE_NAME WHERE topicStreamId = :topicStreamId")
    suspend fun deleteAllTopicStreamMessages(topicStreamId: String)

    @Delete
    suspend fun deleteMessage(message: DatabaseMessageModel)

}