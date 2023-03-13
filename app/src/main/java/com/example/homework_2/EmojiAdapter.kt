package com.example.homework_2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmojiAdapter(
    private val emojiList: List<String>,
    private val onEmojiClickListener: (String) -> Unit
) : RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {

    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emojiView = itemView.findViewById<TextView>(R.id.emoji_item)

        fun bind(emoji: String) {
            emojiView.text = emoji
            emojiView.setOnClickListener {
                onEmojiClickListener(emoji)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_emoji_item, parent, false)
        return EmojiViewHolder(view)
    }

    override fun getItemCount(): Int = emojiList.size

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojiList[position])
    }
}