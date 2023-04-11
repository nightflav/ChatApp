package com.example.homework_2.screens.message

import android.content.Context
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
import com.example.homework_2.R
import com.example.homework_2.databinding.FragmentMessagesBinding
import com.example.homework_2.models.MessageReaction
import com.example.homework_2.utils.Emojis.getEmojis
import com.example.homework_2.utils.dp
import com.example.homework_2.utils.emojiSetNCS
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MessagesFragment : Fragment() {

    private val args: MessagesFragmentArgs by navArgs()

    private val streamId by lazy {
        args.streamId
    }
    private val topicName by lazy {
        args.topicName
    }
    private val streamName by lazy {
        args.streamName
    }

    private val viewModel: MessagesViewModel by viewModels {
        MessagesViewModel.Factory(
            topicName = topicName,
            streamId = streamId,
            streamName = streamName
        )
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
            context,
            topicName,
            { reaction, msgId ->
                setReactionOnClickListener(reaction, msgId)
            }
        )

        binding.btnTmpRefreshMessages.setOnClickListener {
            lifecycleScope.launch {
                viewModel.messagesChannel.send(MessagesIntents.UpdateMessagesIntent)
            }
        }
        setupToolbar()
        initRecyclerView()
        lifecycleScope.launch {
            viewModel.messagesChannel.send(MessagesIntents.InitMessagesIntent)
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
        val rvLayoutManager = LinearLayoutManager(requireContext())
        rvLayoutManager.generateDefaultLayoutParams()
        binding.rvChat.layoutManager = rvLayoutManager
        (binding.rvChat.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
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
                            content = binding.etMessage.text.toString()
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

    private fun render(state: MessagesScreenState) {
        when (state) {
            MessagesScreenState.Error -> {
                binding.apply {
                    tvMessagesError.isVisible = true
                    pbMessages.isVisible = false
                    rvChat.isVisible = false
                }
            }
            is MessagesScreenState.Success -> {
                binding.apply {
                    tvMessagesError.isVisible = false
                    rvChat.isVisible = true
                    pbMessages.isVisible = false
                    msgAdapter.submitList(state.messages)
                }
            }
            MessagesScreenState.Init -> {}
            MessagesScreenState.Loading -> {
                binding.apply {
                    tvMessagesError.isVisible = false
                    rvChat.isVisible = false
                    pbMessages.isVisible = true
                }
            }
        }
    }
}