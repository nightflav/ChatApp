package com.example.tinkoff_chat_app.screens.message

import android.content.Context
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.tinkoff_chat_app.R
import com.example.tinkoff_chat_app.models.ui_models.MessageModel
import com.example.tinkoff_chat_app.models.ui_models.MessageReaction
import com.example.tinkoff_chat_app.network.downloader.Downloader
import com.example.tinkoff_chat_app.utils.Emojis.UNKNOWN_EMOJI
import com.example.tinkoff_chat_app.utils.MessageTypes.SENDER
import com.example.tinkoff_chat_app.utils.MsgAdapterConsts.ADD_REACTION_ID
import com.example.tinkoff_chat_app.utils.MsgAdapterConsts.CHANGE_MESSAGE_TOPIC_ID
import com.example.tinkoff_chat_app.utils.MsgAdapterConsts.COPY_MESSAGE_ID
import com.example.tinkoff_chat_app.utils.MsgAdapterConsts.DELETE_MESSAGE_ID
import com.example.tinkoff_chat_app.utils.MsgAdapterConsts.EDIT_MESSAGE_ID
import com.example.tinkoff_chat_app.utils.Network.AUTH_KEY
import com.example.tinkoff_chat_app.utils.dp
import com.example.tinkoff_chat_app.views.EmojiView
import com.example.tinkoff_chat_app.views.MessageViewGroup
import com.example.tinkoff_chat_app.views.ReactionsViewGroup
import com.google.android.material.imageview.ShapeableImageView
import kotlin.random.Random

class MessageAdapter(
    private val onMessageLongClickListener: (MessageModel, Boolean) -> Unit,
    private val context: Context,
    private val onReactionClickListener: (MessageReaction, Int) -> Unit,
    private val onTopicItemClickListener: (String) -> Unit,
    private val downloader: Downloader
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages: List<MessageModel> = emptyList()

    fun getTopMessageId() =
        messages.firstOrNull { !it.isDataSeparator && !it.isTopicSeparator }?.message_id

    fun getMessageByPosition(position: Int) = messages[position]

    private companion object {
        private const val RECEIVER_TYPE = 1
        private const val SENDER_TYPE = 0
        private const val DATE_SEPARATOR_TYPE = -1
        private const val TOPIC_SEPARATOR_TYPE = -2
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            message.isDataSeparator -> DATE_SEPARATOR_TYPE
            message.isTopicSeparator -> TOPIC_SEPARATOR_TYPE
            message.user_id == SENDER -> SENDER_TYPE
            else -> RECEIVER_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            RECEIVER_TYPE -> {
                val msgReceivedView = MessageViewGroup(parent.context)
                ReceivedMessageViewHolder(msgReceivedView)
            }
            SENDER_TYPE -> {
                val msgSentView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_sent_layout, parent, false)
                SentMessageViewHolder(msgSentView)
            }
            DATE_SEPARATOR_TYPE -> {
                val dateSeparatorView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.data_separator_layout, parent, false)
                DataSeparatorViewHolder(dateSeparatorView)
            }
            else -> {
                val topicSeparatorView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.topic_separator_layout, parent, false)
                TopicSeparatorViewHolder(topicSeparatorView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReceivedMessageViewHolder -> {
                holder.bind(messages[position])
            }
            is SentMessageViewHolder -> {
                holder.bind(messages[position])
            }
            is DataSeparatorViewHolder -> {
                holder.bind(messages[position])
            }
            is TopicSeparatorViewHolder -> {
                holder.bind(messages[position])
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class DataSeparatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateSepTv = itemView.findViewById<TextView>(R.id.date_separator)

        fun bind(msg: MessageModel) {
            dateSepTv.text = msg.date
        }
    }

    inner class TopicSeparatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateSepTv = itemView.findViewById<TextView>(R.id.topic_separator)

        fun bind(msg: MessageModel) {
            dateSepTv.text = msg.topic
            val nextColor = when (Random.nextInt() % 4) {
                0 -> {
                    R.color.color_range_501_inf
                }
                1 -> {
                    R.color.color_range_51_100
                }
                2 -> {
                    R.color.color_range_101_250
                }
                else -> {
                    R.color.color_range_251_500
                }
            }
            val color = ResourcesCompat.getColor(context.resources, nextColor, context.theme)
            itemView.setBackgroundColor(color)
            itemView.setOnClickListener {
                onTopicItemClickListener(msg.topic)
            }
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {
        private val msgVg = itemView as MessageViewGroup
        fun bind(msg: MessageModel) {
            when {
                msg.containsImage -> {
                    msgVg.attachedImage.isVisible = true
                    msgVg.message.isVisible = false
                    msgVg.attachedFileImage.isVisible = false
                    val imageUrl = GlideUrl(
                        msg.attachedImageUrl,
                        LazyHeaders.Builder().addHeader(
                            "Authorization", AUTH_KEY
                        ).build()
                    )
                    Glide.with(context)
                        .load(imageUrl)
                        .transform(RoundedCorners(16))
                        .into(msgVg.imageToLoad)

                    msgVg.attachedImage.setOnClickListener {
                        downloadImageFile(msg)
                    }
                    msgVg.attachedFileImage.setOnLongClickListener {
                        onMessageLongClickListener(msg, false)
                        true
                    }
                }
                msg.containsDoc -> {
                    msgVg.attachedImage.isVisible = false
                    msgVg.message.isVisible = false
                    msgVg.attachedFileImage.isVisible = true

                    msgVg.attachedFileImage.setOnClickListener {
                        downloadDocFile(msg)
                    }
                    msgVg.attachedFileImage.setOnLongClickListener {
                        onMessageLongClickListener(msg, false)
                        true
                    }
                }
                else -> {
                    msgVg.attachedImage.isVisible = false
                    msgVg.message.isVisible = true
                    msgVg.attachedFileImage.isVisible = false
                    msgVg.linearLayout.setOnClickListener {
                        onMessageLongClickListener(msg, false)
                    }
                }
            }
            msgVg.linearLayout.setOnCreateContextMenuListener(this)
            Glide.with(context)
                .load(msg.avatarUri)
                .transform(RoundedCorners(16))
                .centerCrop()
                .into(msgVg.image)
            val msgId = msg.message_id
            val reactions = msg.reactions
            msgVg.reactions.setMaxSpace(277f.dp(context).toInt())
            if (reactions.isEmpty())
                msgVg.reactions.visibility = View.GONE
            else
                msgVg.reactions.visibility = View.VISIBLE
            msgVg.reactions.addReactions(reactions) {
                onReactionClickListener(it, msgId)
                notifyItemChanged(messages.map { msg -> msg.message_id }.indexOf(msgId))
            }
            msgVg.name.text = msg.senderName
            msgVg.message.text = msg.msg
            msgVg.reactions.findViewById<ImageView>(R.id.addButton).setOnClickListener {
                onMessageLongClickListener(msg, true)
            }
        }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu.add(adapterPosition, DELETE_MESSAGE_ID, 1, R.string.delete_message)
            menu.add(adapterPosition, COPY_MESSAGE_ID, 2, R.string.copy_message)
            menu.add(adapterPosition, ADD_REACTION_ID, 3, R.string.add_reaction)
        }
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {
        private val msgSent = itemView.findViewById<TextView>(R.id.tv_sent_message)
        private val imageHolder =
            itemView.findViewById<ShapeableImageView>(R.id.iv_image_message_sent)
        private val tapToDownload =
            itemView.findViewById<TextView>(R.id.tv_sent_message_download_attachment)
        private val reactionsSent =
            itemView.findViewById<ReactionsViewGroup>(R.id.react_sent_message)

        fun bind(msg: MessageModel) {
            when {
                msg.containsImage -> {
                    msgSent.isVisible = false
                    imageHolder.isVisible = true
                    val imageUrl = GlideUrl(
                        msg.attachedImageUrl,
                        LazyHeaders.Builder().addHeader(
                            "Authorization", AUTH_KEY
                        ).build()
                    )
                    Glide.with(context)
                        .load(imageUrl)
                        .transform(RoundedCorners(16))
                        .into(imageHolder)
                    imageHolder.setOnClickListener {
                        downloadImageFile(msg)
                    }
                }
                msg.containsDoc -> {
                    msgSent.isVisible = false
                    imageHolder.isVisible = true
                    imageHolder.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.ic_download_file,
                            context.theme
                        )
                    )
                    imageHolder.setOnClickListener {
                        downloadDocFile(msg)
                    }
                }
                else -> {
                    tapToDownload.isVisible = false
                    msgSent.isVisible = true
                    imageHolder.isVisible = false
                }
            }
            msgSent.setOnClickListener {
                onMessageLongClickListener(msg, false)
            }
            msgSent.setOnCreateContextMenuListener(this)
            imageHolder.setOnCreateContextMenuListener(this)
            val msgId = msg.message_id
            msgSent.text = msg.msg
            val reactions = msg.reactions
            reactionsSent.addReactions(reactions) {
                onReactionClickListener(it, msgId)
                notifyItemChanged(messages.map { msg -> msg.message_id }.indexOf(msgId))
            }
            if (reactions.isEmpty()) reactionsSent.visibility =
                View.GONE
            else reactionsSent.visibility = View.VISIBLE
            reactionsSent.findViewById<ImageView>(R.id.addButton).setOnClickListener {
                onMessageLongClickListener(msg, true)
            }
        }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu.add(adapterPosition, EDIT_MESSAGE_ID, 0, R.string.edit_message)
            menu.add(adapterPosition, DELETE_MESSAGE_ID, 1, R.string.delete_message)
            menu.add(adapterPosition, COPY_MESSAGE_ID, 2, R.string.copy_message)
            menu.add(adapterPosition, CHANGE_MESSAGE_TOPIC_ID, 3, R.string.change_message_topic)
            menu.add(adapterPosition, ADD_REACTION_ID, 4, R.string.add_reaction)
        }
    }

    private fun ReactionsViewGroup.addReactions(
        reactions: List<MessageReaction>, onReactionClickListener: (MessageReaction) -> Unit
    ) {
        while (this.childCount != 1) for (child in this.children) if (child is EmojiView) this.removeView(
            child
        )

        for (react in reactions) {
            if (react.reaction.getCodeString() != UNKNOWN_EMOJI) {
                val viewToAdd = EmojiView(context)
                with(viewToAdd) {
                    count = react.count
                    emoji = react.reaction.getCodeString()
                    isSelected = react.isSelected
                    setEmojiBackground()
                    setOnClickListener {
                        onReactionClickListener(react)
                    }
                }
                this.addView(viewToAdd)
            }
        }
    }

    fun submitList(newMessages: List<MessageModel>) {
        val diffUtil = DiffCallback(
            messages,
            newMessages
        )
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        messages = newMessages
        diffResult.dispatchUpdatesTo(this)
    }

    class DiffCallback(
        private val oldList: List<MessageModel>,
        private val newList: List<MessageModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val item1 = oldList[oldItemPosition]
            val item2 = newList[newItemPosition]
            return item1.message_id == item2.message_id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val item1 = oldList[oldItemPosition]
            val item2 = newList[newItemPosition]
            return item1 == item2
        }
    }

    private fun downloadDocFile(msg: MessageModel) {
        try {
            val mimeType = "application/" + when {
                msg.attachedFilename!!.contains(".pdg") -> "pdf"
                msg.attachedFilename.contains(".txt") -> "txt"
                msg.attachedFilename.contains(".doc") -> "doc"
                else -> "*"
            }
            downloader.downloadFile(
                uri = msg.attachedDocUrl!!,
                fileName = msg.attachedFilename,
                mimeType = mimeType
            )
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "An error occurred while downloading file.",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("TAGTAGTAG", "$e")
        }
    }

    private fun downloadImageFile(msg: MessageModel) {
        try {
            val mimeType = "application/" + when {
                msg.attachedFilename!!.contains(".pdg") -> "pdf"
                msg.attachedFilename.contains(".txt") -> "txt"
                msg.attachedFilename.contains(".doc") -> "doc"
                else -> "*"
            }
            Log.d("TAGTAGTAG", "${msg.attachedFilename} ${msg.attachedDocUrl}")
            downloader.downloadFile(
                uri = msg.attachedImageUrl!!,
                fileName = msg.attachedFilename,
                mimeType = mimeType
            )
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "An error occurred while downloading file.",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("TAGTAGTAG", "$e")
        }
    }
}