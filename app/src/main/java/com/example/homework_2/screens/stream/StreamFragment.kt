package com.example.homework_2.screens.stream

import android.content.Context
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
                viewModel.openTopicState.emit(it)
            }
        }
    }

    private val viewModel: StreamViewModel by viewModels()

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

        viewModel.searchState
            .flowWithLifecycle(lifecycle)
            .onEach(::render)
            .launchIn(lifecycleScope)

        return binding.root
    }

    override fun onStart() {
        lifecycleScope.launch {
            viewModel.subscribedListState.emit(true)
        }
        super.onStart()
    }

    private fun initSearch() {
        binding.etChannelsSearch.addTextChangedListener {
            lifecycleScope.launch {
                it?.let {
                    viewModel.searchRequestState.emit(it.toString())
                }
            }
        }
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
            is StreamScreenState.Data -> {
                binding.apply {
                    tvErrorStreams.isVisible = false
                    shimmerStreams.isVisible = false
                    rvStreams.isVisible = true
                    streamAdapter.submitList(state.streams)
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

    private fun initTabView() {
        val selectedTabIndex = if (!viewModel.subscribedListState.value) 0 else 1
        binding.tlSelectChannel.getTabAt(selectedTabIndex)?.select()
        binding.tlSelectChannel.addOnTabSelectedListener(getTabListener())
    }

    private fun getTabListener() =
        object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val showSubs = tab?.position == 0
                lifecycleScope.launch {
                    viewModel.subscribedListState.emit(showSubs)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        }

    private fun initRecyclerView(context: Context) {
        binding.rvStreams.adapter = streamAdapter
        val rvLayoutManager = LinearLayoutManager(context)
        rvLayoutManager.generateDefaultLayoutParams()
        binding.rvStreams.layoutManager = rvLayoutManager
        (binding.rvStreams.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false

    }
}