package com.example.homework_2.di

import com.example.homework_2.di.screen_components.contacts.ContactsRepositoryModule
import com.example.homework_2.di.screen_components.contacts.ContactsSubcomponent
import com.example.homework_2.di.screen_components.messages.MessageRepositoryModule
import com.example.homework_2.di.screen_components.streams.StreamRepositoryModule
import com.example.homework_2.di.screen_components.streams.StreamSubcomponent
import com.example.homework_2.screens.profile.ProfileFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [NetworkModule::class, ProfileModule::class, ViewModelModule::class,
    ContactsRepositoryModule::class, MessageRepositoryModule::class, StreamRepositoryModule::class]
)
interface ApplicationComponent {

    fun inject(fragment: ProfileFragment)

    fun contactsComponent(): ContactsSubcomponent.Builder

    fun streamComponent(): StreamSubcomponent.Builder

    @Component.Builder
    interface Factory {
        fun build(): ApplicationComponent
    }
}

