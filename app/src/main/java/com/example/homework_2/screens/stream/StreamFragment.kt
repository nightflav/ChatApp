package com.example.homework_2.screens.stream

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.homework_2.databinding.FragmentChannelsBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StreamFragment : Fragment() {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!
    private val streamAdapter: StreamAdapter by lazy {
        StreamAdapter(requireContext(), findNavController()) {
            lifecycleScope.launch {
                viewModel.streamChannel.send(
                    StreamIntents.UpdateStreamSelectedState(
                        it,
                        showSubscribed
                    )
                )
            }
        }
    }

    private val viewModel: StreamViewModel by viewModels()
    private val showSubscribed: Boolean
        get() = binding.tlSelectChannel.selectedTabPosition == 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        val context = requireContext()
        initSearch()
        initRecyclerView(context)
        initTabView()
        viewModel.screenState
            .flowWithLifecycle(lifecycle)
            .onEach(::render)
            .launchIn(lifecycleScope)

        return binding.root
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            lifecycleScope.launch {
                s?.let {
                    val request = it.toString()
                    viewModel.streamChannel.send(
                        StreamIntents.SearchForStreamIntent(
                            request = request,
                            showSubscribed = showSubscribed
                        )
                    )
                }
            }
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun initSearch() {
        binding.etChannelsSearch.addTextChangedListener(textWatcher)
    }

    private val tabListener =
        object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                lifecycleScope.launch {
                    viewModel.streamChannel.send(
                        StreamIntents.ChangeSubscribedStreamsState(
                            showSubscribed
                        )
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        }

    private fun initTabView() {
        binding.tlSelectChannel.addOnTabSelectedListener(tabListener)
    }

    private fun initRecyclerView(context: Context) {
        binding.rvStreams.adapter = streamAdapter
        val rvLayoutManager = LinearLayoutManager(context)
        rvLayoutManager.generateDefaultLayoutParams()
        binding.rvStreams.layoutManager = rvLayoutManager
        (binding.rvStreams.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }

    private fun render(state: StreamScreenState) {
        when (state) {
            StreamScreenState.Error -> {
                binding.apply {
                    tvErrorStreams.isVisible = true
                    shimmerStreams.isVisible = false
                    rvStreams.isVisible = false
                }
            }
            is StreamScreenState.Success -> {
                binding.apply {
                    tvErrorStreams.isVisible = false
                    shimmerStreams.isVisible = false
                    rvStreams.isVisible = true

                    streamAdapter.submitList(state.streams)
                    val tabPosition = if (state.showSubscribed) 0 else 1
                    val tab = tlSelectChannel.getTabAt(tabPosition)
                    tab!!.select()
                }
            }
            StreamScreenState.Init -> {}
            StreamScreenState.Loading -> {
                binding.apply {
                    tvErrorStreams.isVisible = false
                    shimmerStreams.isVisible = true
                    rvStreams.isVisible = false
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.etChannelsSearch.removeTextChangedListener(textWatcher)
        _binding = null
        super.onDestroyView()
    }
}