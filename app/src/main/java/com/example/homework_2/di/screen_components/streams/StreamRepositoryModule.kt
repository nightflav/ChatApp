package com.example.homework_2.di.screen_components.streams

import com.example.homework_2.di.screen_components.messages.MessagesSubcomponent
import com.example.homework_2.network.ChatApi
import com.example.homework_2.domain.repository.streams_repository.StreamsRepository
import com.example.homework_2.domain.repository.streams_repository.StreamsRepositoryImpl
import dagger.Module
import dagger.Provides

@Module(subcomponents = [MessagesSubcomponent::class])
class StreamRepositoryModule {

    @Provides
    fun provideStreamRepository(chatApi: ChatApi): StreamsRepository =
        StreamsRepositoryImpl(chatApi)

}