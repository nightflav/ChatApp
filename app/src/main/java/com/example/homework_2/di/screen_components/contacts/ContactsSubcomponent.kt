package com.example.homework_2.di.screen_components.contacts

import com.example.homework_2.screens.contacts.ContactsFragment
import dagger.Subcomponent

@ContactsScope
@Subcomponent(
    modules = [ContactsRepositoryModule::class]
)
interface ContactsSubcomponent {

    fun inject(fragment: ContactsFragment)

    @Subcomponent.Builder
    interface Builder {
        fun build(): ContactsSubcomponent
    }

}