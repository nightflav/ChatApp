package com.example.homework_2.screens.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_2.datasource.ProfilesDatasource
import com.example.homework_2.runCatchingNonCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactsViewModel : ViewModel() {

    private val _searchState: MutableStateFlow<ContactsScreenState> =
        MutableStateFlow(ContactsScreenState.Init)
    val searchState get() = _searchState.asStateFlow()
    val searchRequestState: MutableSharedFlow<String> = MutableSharedFlow()

    init {
        subscribeToProfileSearch()
        viewModelScope.launch {
            _searchState.emit(searchForProfile(""))
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun subscribeToProfileSearch() {
        searchRequestState
            .filter { it.isNotBlank() }
            .distinctUntilChanged()
            .debounce(500L)
            .flatMapLatest { flow { emit(searchForProfile(it)) } }
            .onEach { _searchState.emit(it) }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private suspend fun searchForProfile(request: String): ContactsScreenState {
        val result = runCatchingNonCancellation {
            _searchState.emit(ContactsScreenState.Loading)
            ProfilesDatasource.getContactsWithSearchRequest(request)
        }.getOrNull()

        return result?.let { ContactsScreenState.Profiles(it) }
            ?: ContactsScreenState.Error
    }
}