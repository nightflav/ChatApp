package com.example.tinkoff_chat_app.data.topics

import androidx.room.*
import com.example.tinkoff_chat_app.models.db_models.DatabaseTopicModel
import com.example.tinkoff_chat_app.utils.LocalData.TOPIC_TABLE_NAME

@Dao
interface TopicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTopics(topics: List<DatabaseTopicModel>)

    @Query(value = "SELECT * FROM $TOPIC_TABLE_NAME WHERE parentId = :fromStream")
    suspend fun getTopics(fromStream: Int): List<DatabaseTopicModel>

    @Query("DELETE FROM $TOPIC_TABLE_NAME")
    suspend fun deleteAllTopics()

    @Query("DELETE FROM $TOPIC_TABLE_NAME WHERE parentId = :streamId")
    suspend fun deleteAllTopicsOfCurrentStream(streamId: Int)

    @Delete
    suspend fun deleteTopic(topic: DatabaseTopicModel)

}