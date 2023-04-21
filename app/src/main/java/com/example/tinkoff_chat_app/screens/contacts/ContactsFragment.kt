package com.example.tinkoff_chat_app.screens.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinkoff_chat_app.databinding.FragmentContactsBinding
import com.example.tinkoff_chat_app.di.ViewModelFactory
import com.example.tinkoff_chat_app.utils.getAppComponent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactsFragment : Fragment() {
    private val contactsAdapter: ContactsAdapter by lazy {
        ContactsAdapter(
            requireContext()
        )
    }

    private var _binding: FragmentContactsBinding? = null
    private val binding: FragmentContactsBinding
        get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ContactsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val appComponent = getAppComponent()
        appComponent.contactsComponent().create().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        val context = requireContext()

        binding.rvContacts.layoutManager = LinearLayoutManager(context)
        binding.rvContacts.adapter = contactsAdapter
        initSearch()
        lifecycleScope.launch {
            viewModel.contactsChannel.send(
                ContactsIntents.InitContacts
            )
        }

        viewModel.screenState
            .flowWithLifecycle(lifecycle)
            .onEach(::render)
            .launchIn(lifecycleScope)

        return binding.root
    }

    private fun initSearch() {
        binding.etUsersSearch.addTextChangedListener {
            lifecycleScope.launch {
                it?.let {
                    viewModel.contactsChannel
                        .send(ContactsIntents.SearchForUserIntent(request = it.toString()))
                }
            }
        }
    }

    private fun render(state: ContactsScreenState) {
        when (state) {
            is ContactsScreenState.Error -> {
                binding.apply {
                    tvErrorStreams.isVisible = true
                    shimmerContacts.isVisible = false
                    rvContacts.isVisible = false
                    tvErrorStreams.text = state.e.message
                }
            }
            is ContactsScreenState.Success -> {
                binding.apply {
                    tvErrorStreams.isVisible = false
                    shimmerContacts.isVisible = false
                    rvContacts.isVisible = true
                    contactsAdapter.submitList(state.profiles)
                }
            }
            ContactsScreenState.Init -> {}
            ContactsScreenState.Loading -> {
                binding.apply {
                    tvErrorStreams.isVisible = false
                    shimmerContacts.isVisible = true
                    rvContacts.isVisible = false
                }
            }
        }
    }
}