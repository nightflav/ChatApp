package com.example.homework_2.screens.stream

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.R
import com.example.homework_2.models.streamScreenModels.StreamModel
import com.example.homework_2.models.streamScreenModels.StreamScreenItem
import com.example.homework_2.models.streamScreenModels.TopicModel

class StreamAdapter(
    private val context: Context,
    private val navController: NavController,
    private val onStreamClickListener: (StreamModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        private const val TOPIC_VIEW_TYPE = 0
        private const val CHANNEL_VIEW_TYPE = 1
    }

    private var dataList = emptyList<StreamScreenItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TOPIC_VIEW_TYPE -> TopicViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.topic_layout, parent, false)
            )
            CHANNEL_VIEW_TYPE -> StreamViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.stream_layout, parent, false)
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
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is TopicModel -> TOPIC_VIEW_TYPE
            is StreamModel -> CHANNEL_VIEW_TYPE
            else -> throw IllegalStateException()
        }
    }

    fun submitList(streams: List<StreamScreenItem>) {
        val diffUtil = DiffCallback(
            dataList,
            streams
        )
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        diffResult.dispatchUpdatesTo(this)
        dataList = streams
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
            return if (item1 is StreamModel && item2 is StreamModel)
                item1.id == item2.id
            else
                if (item1 is TopicModel && item2 is TopicModel)
                    item1.id == item2.id
                else
                    false
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val item1 = oldList[oldItemPosition]
            val item2 = newList[newItemPosition]
            return item1 == item2
        }
    }

    inner class StreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val streamName = itemView.findViewById<TextView>(R.id.tv_stream_name)
        private val streamArrow = itemView.findViewById<ImageView>(R.id.iv_stream_button)

        fun bind(stream: StreamModel) {
            itemView.setOnClickListener {
                onStreamClickListener(stream)
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
                val action =
                    StreamFragmentDirections.actionChannelsFragmentToMessagesFragment(
                        topicId = topic.id,
                        streamId = topic.parentId,
                        topicName = topic.name,
                        streamName = topic.parentName
                    )
                navController.navigate(action)
            }
        }
    }
}