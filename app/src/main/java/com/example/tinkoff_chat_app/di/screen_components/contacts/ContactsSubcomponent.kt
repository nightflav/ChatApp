package com.example.tinkoff_chat_app.di.screen_components.contacts

import com.example.tinkoff_chat_app.screens.contacts.ContactsFragment
import dagger.Subcomponent

@ContactsScope
@Subcomponent
interface ContactsSubcomponent {

    fun inject(fragment: ContactsFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): ContactsSubcomponent
    }

}