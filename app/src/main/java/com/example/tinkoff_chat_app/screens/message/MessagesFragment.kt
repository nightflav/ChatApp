package com.example.tinkoff_chat_app.screens.message

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.tinkoff_chat_app.R
import com.example.tinkoff_chat_app.databinding.FragmentMessagesBinding
import com.example.tinkoff_chat_app.di.ViewModelFactory
import com.example.tinkoff_chat_app.models.MessageReaction
import com.example.tinkoff_chat_app.utils.Emojis.emojiSetNCS
import com.example.tinkoff_chat_app.utils.Emojis.getEmojis
import com.example.tinkoff_chat_app.utils.Network.MESSAGES_TO_LOAD
import com.example.tinkoff_chat_app.utils.dp
import com.example.tinkoff_chat_app.utils.getAppComponent
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


class MessagesFragment : Fragment() {

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    private val args: MessagesFragmentArgs by navArgs()

    private val topicName by lazy {
        args.topicName
    }
    private val streamName by lazy {
        args.streamName
    }
    private val streamId by lazy {
        args.streamId
    }

    private var isLoading = false
    private var allMessagesLoaded = false

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by viewModels<MessagesViewModel> {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val msgComponent = getAppComponent().messageComponent().create()
        msgComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    private lateinit var msgAdapter: MessageAdapter
    private var bottomSheetDialog: BottomSheetDialog? = null

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val context = requireContext()
        msgAdapter = MessageAdapter(
            { msgId -> getReaction(msgId, context) },
            context
        ) { reaction, msgId ->
            setReactionOnClickListener(reaction, msgId)
        }
        binding.btnTmpRefreshMessages.setOnClickListener {
            lifecycleScope.launch {
                viewModel.messagesChannel.send(
                    MessagesIntents.UpdateMessagesIntent
                )
            }
        }
        setupToolbar()
        initRecyclerView()
        lifecycleScope.launch {
            viewModel.messagesChannel.send(
                MessagesIntents.InitMessagesIntent(
                    streamName,
                    topicName,
                    streamId
                )
            )
            if (isNetworkAvailable(context))
                viewModel.messagesChannel.send(
                    MessagesIntents.LoadMessagesIntent(
                        MESSAGES_TO_LOAD,
                        msgAdapter.getTopMessageId()?.toInt()
                    )
                )
        }

        viewModel.screenState
            .flowWithLifecycle(lifecycle)
            .onEach(::render)
            .launchIn(lifecycleScope)

        setSendButtonChange(context)
        setSendButtonOnClickListener()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        binding.ivToolbarBack.setOnClickListener {
            val action = MessagesFragmentDirections.actionMessagesFragmentToChannelsFragment()
            findNavController().navigate(action)
        }
        binding.tvStreamName.text = getString(
            R.string.stream_name,
            streamName
        )
        binding.tvTopicName.text = topicName
    }

    private fun initRecyclerView() {
        binding.rvChat.adapter = msgAdapter
        val rvLayoutManager = LinearLayoutManager(requireContext()).also {
            it.stackFromEnd = true
        }
        rvLayoutManager.generateDefaultLayoutParams()
        binding.rvChat.layoutManager = rvLayoutManager
        (binding.rvChat.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = true

        binding.rvChat.addOnScrollListener(scrollListener)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val adapter = recyclerView.adapter!! as MessageAdapter
            val messagesLeftOnTop = layoutManager.findFirstCompletelyVisibleItemPosition()
            val topMsgId = adapter.getTopMessageId()

            if (messagesLeftOnTop <= 5 && !isLoading && !allMessagesLoaded && isNetworkAvailable(
                    requireContext()
                )
            ) {
                lifecycleScope.launch {
                    isLoading = true
                    viewModel.messagesChannel.send(
                        MessagesIntents.LoadMessagesIntent(
                            amount = 20,
                            lastMsgId = topMsgId?.toInt()
                        )
                    )
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        }
    }

    private fun setReactionOnClickListener(
        reaction: MessageReaction,
        msgId: String
    ) {
        lifecycleScope.launch {
            viewModel.messagesChannel.send(
                MessagesIntents.ChangeReactionStateIntent(
                    reaction = reaction,
                    msgId = msgId
                )
            )
        }
    }

    private fun getReaction(msgId: String, context: Context) {
        if (bottomSheetDialog?.isShowing == false || bottomSheetDialog == null) {
            val dialogLayout =
                layoutInflater.inflate(
                    R.layout.bsd_select_emoji_layout,
                    LinearLayoutCompat(context)
                )
            bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
            val rvEmojis = dialogLayout.findViewById<RecyclerView>(R.id.rv_emoji_bsd)
            val emojiAdapter = EmojiAdapter(getEmojis()) {
                setReactionOnClickListener(
                    reaction = MessageReaction(
                        emojiSetNCS[getEmojis().indexOf(it)],
                        1,
                        false
                    ),
                    msgId = msgId
                )
                bottomSheetDialog!!.cancel()
            }
            bottomSheetDialog!!.setContentView(dialogLayout)
            rvEmojis.layoutManager = GridLayoutManager(context, 7)
            rvEmojis.adapter = emojiAdapter
            bottomSheetDialog!!.behavior.maxHeight = 700f.dp(context).toInt()
            bottomSheetDialog!!.show()
        }
    }

    private fun setSendButtonOnClickListener() {
        binding.btnSend.setOnClickListener {
            if (binding.etMessage.text!!.isNotEmpty()) {
                lifecycleScope.launch {
                    viewModel.messagesChannel.send(
                        MessagesIntents.SendMessageIntent(
                            content = binding.etMessage.text.toString(),
                        )
                    )
                }
                binding.etMessage.text!!.clear()
            }
        }
    }

    private fun setSendButtonChange(context: Context) {
        binding.etMessage.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (text.isEmpty()) {
                    binding.btnSend.setImageDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.ic_add)
                    )
                } else {
                    binding.btnSend.setImageDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.ic_send)
                    )
                }
            }
        }
    }

    private fun render(state: MessageScreenUiState) {
        when {
            state.error != null -> {
                binding.apply {
                    tvMessagesError.isVisible = true
                    tvMessagesError.text = state.error.message
                    pbMessages.isVisible = false
                    rvChat.isVisible = false
                }
            }

            state.messages != null -> {
                binding.apply {
                    tvMessagesError.isVisible = false
                    rvChat.isVisible = true
                    pbMessages.isVisible = false
                    msgAdapter.submitList(state.messages)
                    isLoading = state.isNewMessagesLoading
                    allMessagesLoaded = state.allMessagesLoaded
                }
            }
            state.isLoading -> {
                binding.apply {
                    tvMessagesError.isVisible = false
                    rvChat.isVisible = false
                    pbMessages.isVisible = true
                }
            }
        }
    }
}