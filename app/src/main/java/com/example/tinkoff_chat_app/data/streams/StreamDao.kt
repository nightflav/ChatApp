package com.example.tinkoff_chat_app.data.streams

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tinkoff_chat_app.utils.LocalData.SUBSCRIBED_STREAM_TABLE_NAME
import com.example.tinkoff_chat_app.utils.LocalData.UNSUBSCRIBED_STREAM_TABLE_NAME

@Dao
interface StreamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStream(stream: DatabaseStreamModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStream(stream: DatabaseSubscribedStreamModel)

    @Query(value = "SELECT * FROM $UNSUBSCRIBED_STREAM_TABLE_NAME")
    suspend fun getAllStreams(): List<DatabaseStreamModel>

    @Query(value = "SELECT * FROM $SUBSCRIBED_STREAM_TABLE_NAME")
    suspend fun getSubStreams(): List<DatabaseStreamModel>

    @Query("SELECT COUNT(name) FROM $SUBSCRIBED_STREAM_TABLE_NAME")
    suspend fun getSubscribedItemCount(): Int

    @Query("SELECT COUNT(name) FROM $UNSUBSCRIBED_STREAM_TABLE_NAME")
    suspend fun getItemCount(): Int

    @Query("DELETE FROM $SUBSCRIBED_STREAM_TABLE_NAME")
    suspend fun deleteAllSubStreams()

    @Query("DELETE FROM $UNSUBSCRIBED_STREAM_TABLE_NAME")
    suspend fun deleteAllStreams()

}