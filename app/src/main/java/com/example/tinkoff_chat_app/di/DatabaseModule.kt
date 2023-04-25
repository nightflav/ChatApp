package com.example.tinkoff_chat_app.di

import android.content.Context
import androidx.room.Room
import com.example.tinkoff_chat_app.data.messages.MessageDao
import com.example.tinkoff_chat_app.data.AppDatabase
import com.example.tinkoff_chat_app.data.streams.StreamDao
import com.example.tinkoff_chat_app.data.topics.TopicDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ChatDB"
        ).build()
    }

    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao = database.getMessageDao()

    @Provides
    fun provideStreamDao(database: AppDatabase): StreamDao = database.getStreamDao()

    @Provides
    fun provideTopicDao(database: AppDatabase): TopicDao = database.getTopicDao()
}