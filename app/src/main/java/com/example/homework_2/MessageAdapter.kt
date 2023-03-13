package com.example.homework_2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.models.Reaction
import com.example.homework_2.models.SingleMessage
import com.example.homework_2.views.EmojiView
import com.example.homework_2.views.MessageViewGroup
import com.example.homework_2.views.ReactionsViewGroup

class MessageAdapter(private val getReaction: (String) -> Unit, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val RECEIVER_TYPE = 0
    private val SENDER_TYPE = 1
    private val SEPARATOR_TYPE = -1

    override fun getItemViewType(position: Int): Int {
        val message = Datasource.getMessages()[position]
        return if (!message.isDataSeparator) {
            if (message.user_id == "user_1")
                SENDER_TYPE
            else
                RECEIVER_TYPE
        } else SEPARATOR_TYPE
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
            else -> {
                val separatorView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.data_separator_layout, parent, false)
                DataSeparatorViewHolder(separatorView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReceivedMessageViewHolder -> {
                holder.bind(Datasource.getMessages()[position])
            }
            is SentMessageViewHolder -> {
                holder.bind(Datasource.getMessages()[position])
            }
            is DataSeparatorViewHolder -> {
                holder.bind(Datasource.getMessages()[position])
            }
        }
    }

    override fun getItemCount(): Int = Datasource.getMessages().size

    inner class DataSeparatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateSepTv = itemView.findViewById<TextView>(R.id.date_separator)

        fun bind(msg: SingleMessage) {
            dateSepTv.text = msg.date
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val msgVg = itemView as MessageViewGroup

        fun bind(msg: SingleMessage) {
            val msgId = msg.message_id
            msgVg.reactions.setMaxSpace(277f.dp(context).toInt())
            if (Datasource.getReactions(msgId).isEmpty())
                msgVg.reactions.visibility = View.GONE
            else
                msgVg.reactions.visibility = View.VISIBLE
            msgVg.reactions.addReactions(Datasource.getReactions(msgId)) {
                Datasource.changeReactionSelectedState(
                    msgId = msgId,
                    reaction = it.reaction
                )
                notifyItemChanged(Datasource.getMessages().map { msg -> msg.message_id }
                    .indexOf(msgId))
            }
            msgVg.name.text = msg.senderName
            msgVg.message.text = msg.msg
            msgVg.setOnLongClickListener {
                getReaction(msgId)
                true
            }
            msgVg.reactions.findViewById<ImageView>(R.id.addButton).setOnClickListener {
                getReaction(msgId)
            }
        }
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val llSentMessage = itemView.findViewById<LinearLayout>(R.id.rv_sent_message)
        private val msgSent = itemView.findViewById<TextView>(R.id.tv_sent_message)
        private val reactionsSent =
            itemView.findViewById<ReactionsViewGroup>(R.id.react_sent_message)

        fun bind(msg: SingleMessage) {
            val msgId = msg.message_id
            msgSent.text = msg.msg
            reactionsSent.addReactions(Datasource.getReactions(msgId)) {
                Datasource.changeReactionSelectedState(
                    reaction = it.reaction,
                    msgId = msgId
                )
                notifyItemChanged(Datasource.getMessages().map { msg -> msg.message_id }
                    .indexOf(msgId))
            }
            if (Datasource.getReactions(msgId).isEmpty())
                reactionsSent.visibility = View.GONE
            else
                reactionsSent.visibility = View.VISIBLE

            llSentMessage.setOnLongClickListener {
                getReaction(msgId)
                true
            }
            reactionsSent.findViewById<ImageView>(R.id.addButton).setOnClickListener {
                getReaction(msgId)
            }
        }
    }

    private fun ReactionsViewGroup.addReactions(
        reactions: List<Reaction>,
        onReactionClickListener: (Reaction) -> Unit
    ) {
        while (this.childCount != 1)
            for (child in this.children)
                if (child is EmojiView)
                    this.removeView(child)

        for (react in reactions) {
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