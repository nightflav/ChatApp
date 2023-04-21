package com.example.tinkoff_chat_app.di

import com.example.tinkoff_chat_app.domain.repository.contacts_repository.ContactsRepository
import com.example.tinkoff_chat_app.domain.repository.contacts_repository.ContactsRepositoryImpl
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepositoryImpl
import com.example.tinkoff_chat_app.domain.repository.profile_repository.ProfileRepository
import com.example.tinkoff_chat_app.domain.repository.profile_repository.ProfileRepositoryImpl
import com.example.tinkoff_chat_app.domain.repository.streams_repository.StreamsRepository
import com.example.tinkoff_chat_app.domain.repository.streams_repository.StreamsRepositoryImpl
import com.example.tinkoff_chat_app.network.ChatApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideProfileRepository(chatApi: ChatApi): ProfileRepository =
        ProfileRepositoryImpl(chatApi)

    @Provides
    fun provideMessageRepository(chatApi: ChatApi): MessagesRepository =
        MessagesRepositoryImpl(chatApi)

    @Singleton
    @Provides
    fun provideContactsRepository(chatApi: ChatApi): ContactsRepository =
        ContactsRepositoryImpl(chatApi)


    @Singleton
    @Provides
    fun provideStreamsRepository(chatApi: ChatApi): StreamsRepository =
        StreamsRepositoryImpl(chatApi)

}