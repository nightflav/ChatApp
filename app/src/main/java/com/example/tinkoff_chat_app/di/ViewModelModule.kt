package com.example.tinkoff_chat_app.di

import androidx.lifecycle.ViewModel
import com.example.tinkoff_chat_app.screens.contacts.ContactsViewModel
import com.example.tinkoff_chat_app.screens.message.MessagesViewModel
import com.example.tinkoff_chat_app.screens.profile.ProfileViewModel
import com.example.tinkoff_chat_app.screens.stream.StreamViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(StreamViewModel::class)
    fun streamViewModel(viewModel: StreamViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactsViewModel::class)
    fun contactsViewModel(viewModel: ContactsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    fun profileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessagesViewModel::class)
    fun messageViewModel(viewModel: MessagesViewModel): ViewModel
}