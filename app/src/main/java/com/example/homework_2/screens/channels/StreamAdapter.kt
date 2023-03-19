package com.example.homework_2.screens.channels

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.Datasource
import com.example.homework_2.R
import com.example.homework_2.models.Stream
import com.example.homework_2.models.Topic

class StreamAdapter(
    private val dataList: List<Any>,
    private val context: Context,
    private val navController: NavController
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        private const val TOPIC_VIEW_TYPE = 0
        private const val CHANNEL_VIEW_TYPE = 1
    }

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
                holder.bind(dataList[position] as Stream)
            }
            is TopicViewHolder -> {
                holder.bind(dataList[position] as Topic)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is Topic -> TOPIC_VIEW_TYPE
            else -> CHANNEL_VIEW_TYPE
        }
    }

    inner class StreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val streamName = itemView.findViewById<TextView>(R.id.tv_stream_name)
        private val streamArrow = itemView.findViewById<ImageView>(R.id.iv_stream_button)

        fun bind(stream: Stream) {
            streamName.text = stream.name
            val imageToShow = if (stream.isSelected)
                AppCompatResources.getDrawable(context, R.drawable.ic_close_arrow)
            else
                AppCompatResources.getDrawable(context, R.drawable.ic_drop_down_arrow)
            streamArrow.setImageDrawable(imageToShow)

            streamArrow.setOnClickListener {
                Datasource.changeStreamSelectedState(streamId = stream.id)
                notifyItemChanged(Datasource.getStreams().indexOf(stream))
                if(stream.isSelected)
                    notifyItemRangeInserted(Datasource.getStreams().indexOf(stream) + 1, stream.topics.size)
                else
                    notifyItemRangeRemoved(Datasource.getStreams().indexOf(stream) + 1, stream.topics.size)
            }
        }
    }

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val topicName = itemView.findViewById<TextView>(R.id.topic_name)
        private val topicMsgCount = itemView.findViewById<TextView>(R.id.topic_msg_count)

        fun bind(topic: Topic) {
            topicName.text = topic.name
            topicMsgCount.text = topic.msgCount.toString()
            when(topic.msgCount) {
                in 0..50 -> itemView.setBackgroundColor(getColor(context, R.color.color_range_0_50))
                in 51..100 -> itemView.setBackgroundColor(getColor(context, R.color.color_range_51_100))
                in 101..250 -> itemView.setBackgroundColor(getColor(context, R.color.color_range_101_250))
                in 251..500 -> itemView.setBackgroundColor(getColor(context, R.color.color_range_251_500))
                else -> itemView.setBackgroundColor(getColor(context, R.color.color_range_501_inf))
            }
            itemView.setOnClickListener {
                if(Datasource.containsTopic(topic.id)) {
                    val action =
                        ChannelsFragmentDirections.actionChannelsFragmentToMessagesFragment(
                            topicId = topic.id,
                            streamId = topic.parentId
                        )
                    navController.navigate(action)
                }
            }
        }
    }
}