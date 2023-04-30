package com.example.tinkoff_chat_app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tinkoff_chat_app.models.db_models.DatabaseMessageModel
import com.example.tinkoff_chat_app.data.messages.MessageConverters
import com.example.tinkoff_chat_app.data.messages.MessageDao
import com.example.tinkoff_chat_app.data.streams.StreamConverters
import com.example.tinkoff_chat_app.models.db_models.DatabaseStreamModel
import com.example.tinkoff_chat_app.data.streams.StreamDao
import com.example.tinkoff_chat_app.models.db_models.DatabaseTopicModel
import com.example.tinkoff_chat_app.data.topics.TopicDao
import com.example.tinkoff_chat_app.models.db_models.DatabaseSubscriptionModel

@Database(
    entities = [DatabaseMessageModel::class, DatabaseTopicModel::class, DatabaseStreamModel::class, DatabaseSubscriptionModel::class],
    version = 1
)
@TypeConverters(MessageConverters::class, StreamConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getMessageDao(): MessageDao

    abstract fun getStreamDao(): StreamDao

    abstract fun getTopicDao(): TopicDao

}