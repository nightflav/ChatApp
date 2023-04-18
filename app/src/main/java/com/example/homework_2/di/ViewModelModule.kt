package com.example.homework_2.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.homework_2.di.screen_components.streams.StreamSubcomponent
import com.example.homework_2.screens.contacts.ContactsViewModel
import com.example.homework_2.screens.message.MessagesViewModel
import com.example.homework_2.screens.profile.ProfileViewModel
import com.example.homework_2.screens.stream.StreamViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module(
    subcomponents = [
        StreamSubcomponent::class
    ]
)
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

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