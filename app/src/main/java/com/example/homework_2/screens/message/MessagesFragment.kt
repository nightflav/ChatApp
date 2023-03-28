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
import com.example.homework_2.datasource.MessageDatasource
import com.example.homework_2.datasource.StreamDatasource
import com.example.homework_2.dp
import com.example.homework_2.emojiSetNCS
import com.example.homework_2.messages
import com.example.homework_2.models.Reaction
import com.example.homework_2.models.SingleMessage
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MessagesFragment : Fragment() {


    private val args: MessagesFragmentArgs by navArgs()
    private val topicId by lazy {
        args.topicId
    }

    private val viewModel: MessagesViewModel by viewModels {
        MessagesViewModel.Factory(topicId)
    }

    private val streamId by lazy {
        args.streamId
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
            topicId
        )
        setupToolbar()
        initRecyclerView()

        viewModel.screenState
            .flowWithLifecycle(lifecycle)
            .onEach(::render)
            .launchIn(lifecycleScope)

        setSendButtonChange(context)
        setSendButtonOnClickListener()
        return binding.root
    }

    private fun setupToolbar() {
        binding.ivToolbarBack.setOnClickListener {
            val action = MessagesFragmentDirections.actionMessagesFragmentToChannelsFragment()
            findNavController().navigate(action)
        }
        binding.tvStreamName.text = getString(
            R.string.stream_name,
            StreamDatasource.getStreamById(streamId)?.name ?: "null"
        )
        binding.tvTopicName.text =
            getString(R.string.topic_name, StreamDatasource.getTopicById(topicId)?.name ?: "null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        binding.rvChat.adapter = msgAdapter
        val rvLayoutManager = LinearLayoutManager(requireContext())
        rvLayoutManager.generateDefaultLayoutParams()
        binding.rvChat.layoutManager = rvLayoutManager
        (binding.rvChat.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun setMessageOnClickListener(
        msgAdapter: MessageAdapter,
        reaction: Reaction,
        msgId: String
    ) {
        MessageDatasource.addReaction(reaction, msgId, topicId)
        msgAdapter.notifyItemChanged(viewModel.messages.map { it.message_id }
            .indexOf(msgId))
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
            val emojiAdapter = EmojiAdapter(MessageDatasource.getEmojis()) {
                setMessageOnClickListener(
                    msgAdapter = msgAdapter,
                    reaction = Reaction(
                        emojiSetNCS[MessageDatasource.getEmojis().indexOf(it)],
                        1,
                        true
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
            if (binding.etMessage.text?.isNotEmpty() == true) {
                val newMsg = SingleMessage(
                    msg = binding.etMessage.text.toString(),
                    reactions = mutableListOf(),
                    user_id = "user_1",
                    senderName = "Yaroslav",
                    message_id = (messages.size + 1).toString()
                )
                lifecycleScope.launch { viewModel.newSentMessage.emit(newMsg) }
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

    private fun render(state: MessageScreenState) {
        when (state) {
            MessageScreenState.Error -> {
                binding.apply {
                    tvMessagesError.isVisible = true
                    pbMessages.isVisible = false
                    rvChat.isVisible = false
                }
            }
            is MessageScreenState.Data -> {
                binding.apply {
                    tvMessagesError.isVisible = false
                    rvChat.isVisible = true
                    pbMessages.isVisible = false
                    msgAdapter.submitList(state.messages)
                }
            }
            MessageScreenState.Init -> {}
            MessageScreenState.Loading -> {
                binding.apply {
                    tvMessagesError.isVisible = false
                    rvChat.isVisible = false
                    pbMessages.isVisible = true
                }
            }
        }
    }
}