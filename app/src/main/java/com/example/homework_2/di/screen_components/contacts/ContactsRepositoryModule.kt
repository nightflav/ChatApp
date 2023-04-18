package com.example.homework_2.di.screen_components.contacts

import com.example.homework_2.network.ChatApi
import com.example.homework_2.repository.contacts_repository.ContactsRepository
import com.example.homework_2.repository.contacts_repository.ContactsRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class ContactsRepositoryModule {

    @Provides
    fun provideContactsRepository(chatApi: ChatApi): ContactsRepository =
        ContactsRepositoryImpl(chatApi)

}