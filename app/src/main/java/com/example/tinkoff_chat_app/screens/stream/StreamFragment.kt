package com.example.tinkoff_chat_app.screens.stream

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.tinkoff_chat_app.databinding.FragmentChannelsBinding
import com.example.tinkoff_chat_app.di.ViewModelFactory
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.StreamModel
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.StreamScreenItem
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.TopicModel
import com.example.tinkoff_chat_app.utils.getAppComponent
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class StreamFragment : Fragment() {

    private var createNewStreamDialog: StreamCreateDialog? = null
    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!
    private val streamAdapter: StreamAdapter by lazy {
        StreamAdapter(
            context = requireContext(),
            onStreamClickListener = { stream ->
                val action =
                    StreamFragmentDirections.actionChannelsFragmentToMessagesFragment(
                        stream = stream,
                        topicName = null,
                        allTopics = true,
                        topicId = "wrong topic id"
                    )
                findNavController().navigate(action)
            },
            onCreateNewStreamClickListener = { showCreateNewStreamDialog() },
            onTopicClickListener = { stream, topic ->
                val action =
                    StreamFragmentDirections.actionChannelsFragmentToMessagesFragment(
                        stream = stream,
                        topicName = topic.name.lowercase(),
                        allTopics = false,
                        topicId = "wrong topic id"
                    )
                findNavController().navigate(action)
            },
            onOpenStreamClickListener = { stream ->
                lifecycleScope.launch {
                    viewModel.streamChannel.send(
                        StreamIntents.ShowCurrentStreamTopicsIntent(
                            stream
                        ) {
                            showErrorToast("An error occurred while loading topics...")
                        }
                    )
                }
            }
        )
    }

    private
    val showSubscribed: Boolean
        get() = binding.tlSelectChannel.selectedTabPosition == 0

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private
    val viewModel by viewModels<StreamViewModel> {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val streamComponent = getAppComponent().streamComponent().create()
        streamComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

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

    private
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
        }

        override fun afterTextChanged(s: Editable?) {
            lifecycleScope.launch {
                s?.let {
                    val request = it.toString()
                    viewModel.streamChannel.send(
                        StreamIntents.SearchForStreamIntent(
                            request = request,
                        ) {
                            showErrorToast("An error occurred while searching...")
                        }
                    )
                }
            }
        }
    }

    private fun initSearch() {
        binding.etChannelsSearch.addTextChangedListener(textWatcher)
    }

    private
    val tabListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            lifecycleScope.launch {
                viewModel.streamChannel.send(
                    StreamIntents.ShowCurrentStreamsIntent(
                        showSubscribed
                    ) {
                        showErrorToast("An error occurred while loading...")
                    }
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
            true
    }

    private fun render(state: StreamScreenUiState) {
        when {
            state.isStreamsLoading -> {
                binding.apply {
                    tvErrorStreams.isVisible = false
                    shimmerStreams.isVisible = true
                    rvStreams.isVisible = false
                }
            }
            state.error != null -> {
                binding.apply {
                    tvErrorStreams.isVisible = true
                    tvErrorStreams.text = state.error.message
                    shimmerStreams.isVisible = false
                    rvStreams.isVisible = false
                }
            }
            else -> {
                binding.apply {
                    tvErrorStreams.isVisible = false
                    shimmerStreams.isVisible = false
                    rvStreams.isVisible = true
                    streamAdapter.submitList(state.streams!!.applySearchFilter(state.request))
                    val tabPosition = if (state.showSubs) 0 else 1
                    val tab = tlSelectChannel.getTabAt(tabPosition)
                    tab!!.select()
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.etChannelsSearch.removeTextChangedListener(
            textWatcher
        )
        _binding = null
        super.onDestroyView()
    }

    private fun showErrorToast(message: String) =
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()

    private fun showCreateNewStreamDialog() {
        if (!(createNewStreamDialog != null
                    && createNewStreamDialog!!.dialog != null
                    && createNewStreamDialog!!.dialog!!.isShowing
                    && !createNewStreamDialog!!.isRemoving)
        ) {
            createNewStreamDialog =
                StreamCreateDialog.newInstance { streamName, streamDisc, announce ->
                    lifecycleScope.launch {
                        viewModel.streamChannel.send(
                            StreamIntents.CreateNewStreamIntent(
                                name = streamName.take(60),
                                description = streamDisc,
                                announce = announce
                            ) {
                                showErrorToast("Failed to create new stream.")
                            }
                        )
                    }
                }
            createNewStreamDialog!!.show(childFragmentManager, "stream_dialog")
        }
    }

    private fun List<StreamScreenItem>.applySearchFilter(request: String?): List<StreamScreenItem> {
        val result = mutableListOf<StreamScreenItem>()
        if (request.isNullOrBlank() || request.isEmpty()) return this
        for (item in this) {
            if (item is StreamModel)
                if (item.name.contains(request, true)) {
                    result.add(item)
                }
            if (item is TopicModel) {
                if (item.parentName.contains(request, true)) {
                    result.add(item)
                }
            }
        }
        return result
    }

}