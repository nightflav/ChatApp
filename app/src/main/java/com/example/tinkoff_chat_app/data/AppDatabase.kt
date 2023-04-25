package com.example.tinkoff_chat_app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tinkoff_chat_app.data.messages.DatabaseMessageModel
import com.example.tinkoff_chat_app.data.messages.MessageConverters
import com.example.tinkoff_chat_app.data.messages.MessageDao
import com.example.tinkoff_chat_app.data.streams.DatabaseStreamModel
import com.example.tinkoff_chat_app.data.streams.DatabaseSubscribedStreamModel
import com.example.tinkoff_chat_app.data.streams.StreamDao
import com.example.tinkoff_chat_app.data.topics.DatabaseTopicModel
import com.example.tinkoff_chat_app.data.topics.TopicDao

@Database(
    entities = [DatabaseMessageModel::class, DatabaseTopicModel::class, DatabaseStreamModel::class, DatabaseSubscribedStreamModel::class],
    version = 1
)
@TypeConverters(MessageConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getMessageDao(): MessageDao

    abstract fun getStreamDao(): StreamDao

    abstract fun getTopicDao(): TopicDao

}