package com.example.tinkoff_chat_app.data.streams

import androidx.room.*
import com.example.tinkoff_chat_app.models.db_models.DatabaseStreamModel
import com.example.tinkoff_chat_app.models.db_models.DatabaseSubscriptionModel
import com.example.tinkoff_chat_app.utils.LocalData.STREAM_TABLE_NAME
import com.example.tinkoff_chat_app.utils.LocalData.SUBSCRIPTION_TABLE_NAME

@Dao
interface StreamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStreams(streams: List<DatabaseStreamModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStream(stream: DatabaseStreamModel)

    @Delete
    suspend fun deleteStream(stream: DatabaseStreamModel)

    @Query("SELECT * FROM $STREAM_TABLE_NAME WHERE id = :id")
    suspend fun getStreamById(id: Int): DatabaseStreamModel

    @Query("SELECT * FROM $STREAM_TABLE_NAME")
    suspend fun getAllStreams(): List<DatabaseStreamModel>

    @Query("DELETE FROM $STREAM_TABLE_NAME")
    suspend fun deleteAllStreams()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubscriptions(subs: List<DatabaseSubscriptionModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubscription(stream: DatabaseSubscriptionModel)

    @Delete
    suspend fun deleteSubscription(stream: DatabaseSubscriptionModel)

    @Query("SELECT * FROM $SUBSCRIPTION_TABLE_NAME")
    suspend fun getAllSubscriptions(): List<DatabaseSubscriptionModel>

    @Query("DELETE FROM $SUBSCRIPTION_TABLE_NAME")
    suspend fun deleteAllSubscriptions()

    @Query("SELECT * FROM $SUBSCRIPTION_TABLE_NAME WHERE id = :id")
    suspend fun getSubscriptionById(id: Int): DatabaseSubscriptionModel

}