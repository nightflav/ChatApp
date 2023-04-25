package com.example.tinkoff_chat_app.di

import android.content.Context
import com.example.tinkoff_chat_app.di.screen_components.contacts.ContactsSubcomponent
import com.example.tinkoff_chat_app.di.screen_components.messages.MessagesSubcomponent
import com.example.tinkoff_chat_app.di.screen_components.streams.StreamSubcomponent
import com.example.tinkoff_chat_app.screens.profile.ProfileFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [NetworkModule::class, DataModule::class, ViewModelModule::class, DatabaseModule::class]
)
interface ApplicationComponent {

    fun inject(fragment: ProfileFragment)

    fun contactsComponent(): ContactsSubcomponent.Factory

    fun streamComponent(): StreamSubcomponent.Factory

    fun messageComponent(): MessagesSubcomponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): ApplicationComponent
    }

}