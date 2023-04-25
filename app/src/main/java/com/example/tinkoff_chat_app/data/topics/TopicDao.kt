package com.example.tinkoff_chat_app.data.topics

import androidx.room.*
import com.example.tinkoff_chat_app.utils.LocalData.TOPIC_TABLE_NAME

@Dao
interface TopicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTopic(topic: DatabaseTopicModel)

    @Query(
        value = "SELECT * FROM $TOPIC_TABLE_NAME" +
                " WHERE parentName = :fromStream" +
                " ORDER BY id ASC"
    )
    suspend fun getTopics(fromStream: String): List<DatabaseTopicModel>

    @Query("SELECT COUNT(id) FROM $TOPIC_TABLE_NAME")
    suspend fun getItemCount(): Int

    @Query("DELETE FROM $TOPIC_TABLE_NAME")
    suspend fun deleteAllTopics()

    @Delete
    suspend fun deleteTopic(topic: DatabaseTopicModel)

}