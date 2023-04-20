package com.example.homework_2.di.screen_components.messages

import com.example.homework_2.network.ChatApi
import com.example.homework_2.domain.repository.messages_repository.MessageRepositoryImpl
import com.example.homework_2.domain.repository.messages_repository.MessagesRepository
import com.example.homework_2.domain.repository.profile_repository.ProfileRepository
import com.example.homework_2.domain.repository.streams_repository.StreamsRepository
import dagger.Module
import dagger.Provides

@Module
class MessageRepositoryModule {

    @Provides
    fun provideMessageRepository(
        chatApi: ChatApi,
        streamsRepository: StreamsRepository,
        profileRepository: ProfileRepository
    ): MessagesRepository =
        MessageRepositoryImpl(chatApi, profileRepository)

}