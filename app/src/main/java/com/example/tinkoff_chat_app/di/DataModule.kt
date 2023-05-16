package com.example.tinkoff_chat_app.di

import android.content.Context
import android.content.SharedPreferences
import com.example.tinkoff_chat_app.data.messages.MessageDao
import com.example.tinkoff_chat_app.data.streams.StreamDao
import com.example.tinkoff_chat_app.data.topics.TopicDao
import com.example.tinkoff_chat_app.domain.repository.contacts_repository.ContactsRepository
import com.example.tinkoff_chat_app.domain.repository.contacts_repository.ContactsRepositoryImpl
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepositoryImpl
import com.example.tinkoff_chat_app.domain.repository.profile_repository.ProfileRepository
import com.example.tinkoff_chat_app.domain.repository.profile_repository.ProfileRepositoryImpl
import com.example.tinkoff_chat_app.domain.repository.streams_repository.StreamsRepository
import com.example.tinkoff_chat_app.domain.repository.streams_repository.StreamsRepositoryImpl
import com.example.tinkoff_chat_app.domain.repository.topics_repository.TopicsRepository
import com.example.tinkoff_chat_app.domain.repository.topics_repository.TopicsRepositoryImpl
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.network.downloader.Downloader
import com.example.tinkoff_chat_app.network.downloader.DownloaderImpl
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideDownloader(context: Context): Downloader = DownloaderImpl(context)

    @Singleton
    @Provides
    fun provideProfileRepository(chatApi: ChatApi, sp: SharedPreferences): ProfileRepository =
        ProfileRepositoryImpl(chatApi, sp)

    @Singleton
    @Provides
    fun provideContactsRepository(chatApi: ChatApi): ContactsRepository =
        ContactsRepositoryImpl(chatApi)

    @Singleton
    @Provides
    fun provideMessageRepository(chatApi: ChatApi, chatDao: MessageDao): MessagesRepository =
        MessagesRepositoryImpl(chatApi, chatDao)

    @Singleton
    @Provides
    fun provideStreamsRepository(
        chatApi: ChatApi,
        streamDao: StreamDao,
    ): StreamsRepository =
        StreamsRepositoryImpl(chatApi, streamDao)

    @Singleton
    @Provides
    fun provideTopicsRepository(chatApi: ChatApi, topicDao: TopicDao): TopicsRepository =
        TopicsRepositoryImpl(chatApi, topicDao)

    @Singleton
    @Provides
    fun provideSharedPreferencesForProfile(context: Context): SharedPreferences = context
        .getSharedPreferences(SP_PROFILE, Context.MODE_PRIVATE)
}

