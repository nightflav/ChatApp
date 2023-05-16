package com.example.tinkoff_chat_app.screens.stream

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tinkoff_chat_app.R
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.StreamModel
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.StreamScreenItem
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.TopicModel

class StreamAdapter(
    private val context: Context,
    private val onCreateNewStreamClickListener: () -> Unit,
    private val onStreamClickListener: (StreamModel) -> Unit,
    private val onTopicClickListener: (StreamModel, TopicModel) -> Unit,
    private val onOpenStreamClickListener: (StreamModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        private const val TOPIC_VIEW_TYPE = 1
        private const val CHANNEL_VIEW_TYPE = 0
        private const val SHIMMER_VIEW_TYPE = -1
        private const val CREATE_NEW_STREAM_TYPE = 100

        object CreateNewStream : StreamScreenItem() {
            override val adapterId: String
                get() = "unique id for create new stream"
        }
    }

    private var dataList = listOf<StreamScreenItem>(CreateNewStream)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SHIMMER_VIEW_TYPE -> ShimmerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.shimmer_topic_item, parent, false)
            )
            TOPIC_VIEW_TYPE -> TopicViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.topic_layout, parent, false)
            )
            CHANNEL_VIEW_TYPE -> StreamViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.stream_layout, parent, false)
            )
            CREATE_NEW_STREAM_TYPE -> AddNewStreamViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.add_new_stream_layout, parent, false)
            )
            else -> throw java.lang.Exception("No Such View Type")
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StreamViewHolder -> {
                holder.bind(dataList[position] as StreamModel)
            }
            is TopicViewHolder -> {
                holder.bind(dataList[position] as TopicModel)
            }
            is AddNewStreamViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is TopicModel -> if ((dataList[position] as TopicModel).isLoading) SHIMMER_VIEW_TYPE else TOPIC_VIEW_TYPE
            is StreamModel -> CHANNEL_VIEW_TYPE
            CreateNewStream -> CREATE_NEW_STREAM_TYPE
            else -> throw IllegalStateException()
        }
    }

    fun submitList(streams: List<StreamScreenItem>) {
        val tmpStreams = streams.toMutableList()
        tmpStreams.add(CreateNewStream)
        val diffUtil = DiffCallback(
            dataList,
            tmpStreams
        )
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        diffResult.dispatchUpdatesTo(this)
        dataList = tmpStreams
    }

    class DiffCallback(
        private val oldList: List<StreamScreenItem>,
        private val newList: List<StreamScreenItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val item1 = oldList[oldItemPosition]
            val item2 = newList[newItemPosition]
            return item1.adapterId == item2.adapterId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val item1 = oldList[oldItemPosition]
            val item2 = newList[newItemPosition]
            return item1 == item2
        }
    }

    inner class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class StreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val streamName = itemView.findViewById<TextView>(R.id.tv_stream_name)
        private val streamArrow = itemView.findViewById<ImageView>(R.id.iv_stream_button)

        fun bind(stream: StreamModel) {
            itemView.setOnClickListener {
                onStreamClickListener(stream)
            }
            streamArrow.setOnClickListener {
                onOpenStreamClickListener(stream)
            }
            val imageToShow = if (stream.isSelected)
                AppCompatResources.getDrawable(context, R.drawable.ic_close_arrow)
            else
                AppCompatResources.getDrawable(context, R.drawable.ic_drop_down_arrow)
            streamArrow.setImageDrawable(imageToShow)
            streamName.text = stream.name
        }
    }

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val topicName = itemView.findViewById<TextView>(R.id.topic_name)
        private val topicMsgCount = itemView.findViewById<TextView>(R.id.topic_msg_count)

        fun bind(topic: TopicModel) {
            topicName.text = topic.name
            val stream = dataList.lastInstanceBefore<StreamModel>(topic)!!
            topicMsgCount.text = topic.msgCount.toString()
            when (topic.msgCount) {
                in 0..50 -> itemView.setBackgroundColor(getColor(context, R.color.color_range_0_50))
                in 51..100 -> itemView.setBackgroundColor(
                    getColor(
                        context,
                        R.color.color_range_51_100
                    )
                )
                in 101..250 -> itemView.setBackgroundColor(
                    getColor(
                        context,
                        R.color.color_range_101_250
                    )
                )
                in 251..500 -> itemView.setBackgroundColor(
                    getColor(
                        context,
                        R.color.color_range_251_500
                    )
                )
                else -> itemView.setBackgroundColor(getColor(context, R.color.color_range_501_inf))
            }
            itemView.setOnClickListener {
                onTopicClickListener(stream, topic)
            }
        }
    }

    inner class AddNewStreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            itemView.setOnClickListener {
                onCreateNewStreamClickListener()
            }
        }
    }

    private inline fun <reified T> List<*>.lastInstanceBefore(before: Any): T? {
        var currItem: T? = null
        for (item in this) {
            if (item is T)
                currItem = item
            if (item == before)
                return currItem
        }
        return null
    }
}