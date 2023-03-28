package com.example.homework_2.datasource

import com.example.homework_2.*
import com.example.homework_2.models.*
import kotlinx.coroutines.delay


object ProfilesDatasource {

    fun getProfile() =
        UserProfile(
            fullName = "Boris Godunov",
            isActive = false,
            tmpProfilePhoto = getProfilePhoto(),
            meetingStatus = "In a meeting"
        )

    fun getContacts() = contacts

    private fun getProfilePhoto(): Int = R.drawable.ic_launcher_foreground

    suspend fun getContactsWithSearchRequest(request: String): List<UserProfile> {
        delay(500L)
        return contacts.filter {
            it.fullName.contains(request) ||
                    it.email.contains(request) ||
                    (it.email + " " + it.fullName).contains(request) ||
                    (it.fullName + " " + it.email).contains(request)
        }
    }
}