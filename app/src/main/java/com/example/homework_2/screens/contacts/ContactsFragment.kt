package com.example.homework_2.screens.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework_2.databinding.FragmentContactsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {

    private val viewModel: ContactsViewModel by viewModels()
    private val contactsAdapter: ContactsAdapter by lazy {
        ContactsAdapter(
            requireContext()
        )
    }
    private var _binding: FragmentContactsBinding? = null
    private val binding: FragmentContactsBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        val context = requireContext()
        binding.rvContacts.layoutManager = LinearLayoutManager(context)
        binding.rvContacts.adapter = contactsAdapter
        initSearch()

        viewModel.searchState
            .flowWithLifecycle(lifecycle)
            .onEach(::render)
            .launchIn(lifecycleScope)

        return binding.root
    }

    private fun initSearch() {
        binding.etUsersSearch.addTextChangedListener {
            lifecycleScope.launch {
                it?.let {
                    viewModel.searchRequestState.emit(it.toString())
                }
            }
        }
    }

    private fun render(state: ContactsScreenState) {
        when (state) {
            ContactsScreenState.Error -> {
                binding.apply {
                    tvErrorStreams.isVisible = true
                    shimmerContacts.isVisible = false
                    rvContacts.isVisible = false
                }
            }
            is ContactsScreenState.Profiles -> {
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