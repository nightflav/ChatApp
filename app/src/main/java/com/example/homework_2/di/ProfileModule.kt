package com.example.homework_2.di

import com.example.homework_2.network.ChatApi
import com.example.homework_2.domain.repository.profile_repository.ProfileRepository
import com.example.homework_2.domain.repository.profile_repository.ProfileRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ProfileModule {

    @Singleton
    @Provides
    fun provideProfileRepository(chatApi: ChatApi): ProfileRepository =
        ProfileRepositoryImpl(chatApi)

}