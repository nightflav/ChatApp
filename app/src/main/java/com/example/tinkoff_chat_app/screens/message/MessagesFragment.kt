package com.example.tinkoff_chat_app.screens.message

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
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
import com.example.tinkoff_chat_app.models.ui_models.MessageModel
import com.example.tinkoff_chat_app.models.ui_models.MessageReaction
import com.example.tinkoff_chat_app.network.downloader.Downloader
import com.example.tinkoff_chat_app.screens.message.dialogs.ChangeTopicDialog
import com.example.tinkoff_chat_app.screens.message.dialogs.SelectFileTypeDialog
import com.example.tinkoff_chat_app.utils.Emojis.emojiSetNCS
import com.example.tinkoff_chat_app.utils.Emojis.getEmojis
import com.example.tinkoff_chat_app.utils.MsgAdapterConsts.ADD_REACTION_ID
import com.example.tinkoff_chat_app.utils.MsgAdapterConsts.CHANGE_MESSAGE_TOPIC_ID
import com.example.tinkoff_chat_app.utils.MsgAdapterConsts.COPY_MESSAGE_ID
import com.example.tinkoff_chat_app.utils.MsgAdapterConsts.DELETE_MESSAGE_ID
import com.example.tinkoff_chat_app.utils.MsgAdapterConsts.EDIT_MESSAGE_ID
import com.example.tinkoff_chat_app.utils.Network.MESSAGES_TO_LOAD
import com.example.tinkoff_chat_app.utils.dp
import com.example.tinkoff_chat_app.utils.getAppComponent
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
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

    private val stream by lazy {
        args.stream
    }
    private val topicName by lazy {
        args.topicName
    }
    private val streamName by lazy { stream.name }
    private val streamId by lazy { stream.id }
    private val allTopics by lazy {
        args.allTopics
    }
    private val getPhoto =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                attachFile(uri)
            } else
                makeErrorToast("Incorrect file.")
        }
    private val getDocs =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                attachFile(uri)
            } else
                makeErrorToast("Incorrect file or its size is too big.")
        }

    private var isLoading = false
    private var allMessagesLoaded = false
    private var nowEditing = false

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var downloader: Downloader

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
    private var changeTopicDialog: ChangeTopicDialog? = null
    private var selectFileTypeDialog: SelectFileTypeDialog? = null

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val context = requireContext()

        msgAdapter = MessageAdapter(
            onMessageLongClickListener = { msg, onlyReactions ->
                onMessageLongClick(
                    msg,
                    context,
                    onlyReactions
                )
            },
            context = context,
            onReactionClickListener = { reaction, msgId ->
                setReactionOnClickListener(reaction, msgId)
            },
            onTopicItemClickListener = { topicName ->
                val action =
                    MessagesFragmentDirections.actionMessagesFragmentSelf(
                        stream = stream,
                        topicName = topicName,
                        allTopics = false
                    )
                findNavController().navigate(action)
            },
            downloader = downloader,
            isConnectionAvailable = isNetworkAvailable(context)
        )

        binding.llTopicSelection.isVisible = allTopics
        if (allTopics)
            initTopicSelector()
        setupToolbar()
        initRecyclerView()
        registerForContextMenu(binding.rvChat)

        lifecycleScope.launch {
            viewModel.messagesChannel.send(
                MessagesIntents.InitMessagesIntent(
                    streamName,
                    topicName,
                    streamId,
                    allTopics
                )
            )
            if (isNetworkAvailable(context))
                viewModel.messagesChannel.send(
                    MessagesIntents.LoadMessagesIntent(
                        MESSAGES_TO_LOAD,
                        msgAdapter.getTopMessageId()
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

    private fun initTopicSelector() {

    }

    private fun onMessageLongClick(msg: MessageModel, context: Context, onlyReactions: Boolean) {
        showBottomSheetDialog(msg, context, onlyReactions)
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
        (binding.rvChat.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

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
                            lastMsgId = topMsgId
                        )
                    )
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        }
    }

    private fun setReactionOnClickListener(
        reaction: MessageReaction,
        msgId: Int
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

    private fun showBottomSheetDialog(msg: MessageModel, context: Context, onlyReactions: Boolean) {
        if (bottomSheetDialog?.isShowing == false || bottomSheetDialog == null) {
            val dialogLayout = if (!onlyReactions) {
                val layout =
                    layoutInflater.inflate(
                        R.layout.bsd_long_click_on_message,
                        LinearLayoutCompat(context)
                    )
                initButtons(layout, msg)
                layout
            } else {
                layoutInflater.inflate(
                    R.layout.reactions_bsd_layout,
                    LinearLayoutCompat(context)
                )
            }
            bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
            val rvEmojis = dialogLayout.findViewById<RecyclerView>(R.id.rv_emoji_bsd)
            val emojiAdapter = EmojiAdapter(getEmojis()) {
                setReactionOnClickListener(
                    reaction = MessageReaction(
                        emojiSetNCS[getEmojis().indexOf(it)],
                        1,
                        false
                    ),
                    msgId = msg.message_id
                )
                bottomSheetDialog!!.cancel()
            }
            bottomSheetDialog!!.setContentView(dialogLayout)
            rvEmojis.layoutManager = GridLayoutManager(context, 7)
            rvEmojis.adapter = emojiAdapter
            bottomSheetDialog!!.behavior.peekHeight = 250f.dp(context).toInt()
            bottomSheetDialog!!.behavior.maxHeight = 700f.dp(context).toInt()
            bottomSheetDialog!!.show()
        }
    }

    private fun initButtons(v: View, msg: MessageModel) {
        v.findViewById<LinearLayoutCompat>(R.id.llc_copy_message).setOnClickListener {
            requireContext().copyToClipboard(msg.msg)
            bottomSheetDialog!!.cancel()
        }
        v.findViewById<LinearLayoutCompat>(R.id.llc_delete_message).setOnClickListener {
            deleteMessageListener(msg)
            bottomSheetDialog!!.cancel()
        }
        v.findViewById<LinearLayoutCompat>(R.id.llc_edit_message).setOnClickListener {
            editMessageListener(msg)
            bottomSheetDialog!!.cancel()
        }
        v.findViewById<LinearLayoutCompat>(R.id.llc_change_topic).setOnClickListener {
            changeMessageTopicListener(msg)
            bottomSheetDialog!!.cancel()
        }
    }

    private fun setSendButtonOnClickListener() {
        binding.btnSend.setOnClickListener {
            if (binding.etMessage.text!!.isNotEmpty()) {
                val topic = binding.etTopicSelector.text.toString().lowercase()
                if (!nowEditing)
                    if (topic.isEmpty() || topic.isBlank())
                        makeErrorToast("Input topic name!")
                    else
                        lifecycleScope.launch {
                            viewModel.messagesChannel.send(
                                MessagesIntents.SendMessageIntent(
                                    content = binding.etMessage.text.toString(),
                                    topic = topicName?.lowercase()
                                        ?: binding.etTopicSelector.text.toString()
                                ) {
                                    makeErrorToast("An error occurred sending message.")
                                }
                            )
                        }
                else {
                    lifecycleScope.launch {
                        viewModel.messagesChannel.send(
                            MessagesIntents.EditMessageIntent(
                                newMessageContent = binding.etMessage.text.toString(),
                                msgId = binding.etMessage.tag as Int
                            ) { message ->
                                makeErrorToast(message)
                            }
                        )
                    }
                    nowEditing = false
                    binding.etMessage.tag = Any()
                }
                binding.etMessage.text!!.clear()
            } else {
                showSelectFileDialog()
            }
        }
    }

    private fun showSelectFileDialog() {
        if (!(selectFileTypeDialog != null
                    && selectFileTypeDialog!!.dialog != null
                    && selectFileTypeDialog!!.dialog!!.isShowing
                    && !selectFileTypeDialog!!.isRemoving)
        ) {
            selectFileTypeDialog = SelectFileTypeDialog.newInstance(
                docFunc = {
                    getDocs.launch("application/*")
                },
                imageFunc = {
                    getPhoto.launch(PickVisualMediaRequest())
                }
            )
            selectFileTypeDialog!!.show(childFragmentManager, "select_file_type")
        }
    }

    private fun attachFile(uri: Uri) {
        try {
            val typedUri = (uri.toString().replaceBefore(":", "file") + ".${
                getMimeType(
                    requireContext(),
                    uri
                )
            }").toUri()
            val file = typedUri.toFile()
            val bytes = requireActivity().contentResolver.openInputStream(uri)?.buffered()
                ?.use { it.readBytes() }
            lifecycleScope.launch {
                viewModel.messagesChannel.send(
                    MessagesIntents.UploadFileIntent(
                        file = bytes!!,
                        fileName = file.name,
                        topic = binding.etTopicSelector.text.toString()
                    ) {
                        makeErrorToast(it)
                    }
                )
            }
        } catch (e: Exception) {
            Log.d("TAGTAGTAG", "$e")
            makeErrorToast("An error occurred selecting file.")
        }
    }

    private fun getMimeType(context: Context, uri: Uri): String? {
        val extension: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(uri.path?.let { File(it) }).toString())
        }
        return extension
    }

    private fun makeErrorToast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    private fun setSendButtonChange(context: Context) {
        binding.etMessage.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (nowEditing) {
                    binding.btnSend.setImageDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.ic_done)
                    )
                } else
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
                    val shouldScroll = !rvChat.canScrollVertically(1)
                    msgAdapter.submitList(state.messages)
                    if (shouldScroll)
                        rvChat.smoothScrollToPosition(msgAdapter.itemCount)
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val msg = msgAdapter.getMessageByPosition(item.groupId)
        when (item.itemId) {
            EDIT_MESSAGE_ID -> {
                editMessageListener(msg)
            }
            CHANGE_MESSAGE_TOPIC_ID -> {
                showChangeTopicDialog(msg)
            }
            DELETE_MESSAGE_ID -> {
                deleteMessageListener(msg)
            }
            COPY_MESSAGE_ID -> {
                requireContext().copyToClipboard(msg.msg)
            }
            ADD_REACTION_ID -> {
                showBottomSheetDialog(msg, requireContext(), true)
            }
            else -> {}
        }
        return super.onContextItemSelected(item)
    }

    private fun deleteMessageListener(msg: MessageModel) {
        lifecycleScope.launch {
            viewModel.messagesChannel.send(
                MessagesIntents.DeleteMessageIntent(
                    msg.message_id
                )
            )
        }
    }

    private fun editMessageListener(msg: MessageModel) {
        binding.etMessage.setText(msg.msg)
        binding.etMessage.requestFocus()
        nowEditing = true
        binding.etMessage.tag = msg.message_id
        binding.btnSend.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_done
            )
        )
    }

    private fun changeMessageTopicListener(msg: MessageModel) {
        showChangeTopicDialog(msg)
    }

    private fun showChangeTopicDialog(msg: MessageModel) {
        if (!(changeTopicDialog != null
                    && changeTopicDialog!!.dialog != null
                    && changeTopicDialog!!.dialog!!.isShowing
                    && !changeTopicDialog!!.isRemoving)
        ) {
            changeTopicDialog = ChangeTopicDialog.newInstance { newTopicName ->
                lifecycleScope.launch {
                    viewModel.messagesChannel.send(
                        MessagesIntents.ChangeMessageTopicIntent(
                            msgId = msg.message_id,
                            newTopicName = newTopicName.lowercase()
                        ) {
                            makeErrorToast(it)
                        }
                    )
                }
            }
            changeTopicDialog!!.show(childFragmentManager, "message_dialog")
        }
    }

    private fun Context.copyToClipboard(text: CharSequence) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }
}