package com.example.homework_2

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.homework_2.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.messageVg.setOnLongClickListener {
            showFirstReaction(it)
        }

        binding.messageVg.findViewById<ImageView>(R.id.addButton).setOnClickListener {
            addReaction(it)
        }
    }

    private fun getRandomEmoji(): String {
        return when (Random.nextInt() % 4) {
            0 -> Emojis.Smiley.unicode
            1 -> Emojis.Sad.unicode
            2 -> Emojis.Poker.unicode
            else -> Emojis.Fire.unicode
        }
    }

    private enum class Emojis(val unicode: String) {
        Smiley("\uD83D\uDE00"),
        Sad("\uD83D\uDE25"),
        Poker("\uD83D\uDE10"),
        Fire("\uD83D\uDD25")
    }

    private fun showFirstReaction(view: View): Boolean {
        view as ViewGroup
        return if (view.findViewById<ReactionsViewGroup>(R.id.reactions).childCount <= 1) {
            val reactions = view.findViewById<ReactionsViewGroup>(R.id.reactions)
            val emoji = EmojiView(this)
            with(emoji) {
                setEmoji("\uD83D\uDE01")
                setEmojiBackground()
                id = 0
                setOnClickListener {
                    isSelected = !isSelected
                    if (isSelected)
                        increaseCount()
                    else
                        decreaseCount()
                }
            }
            reactions.addView(emoji)
            view.invalidate()
            true
        } else
            true
    }

    private fun addReaction(view: View) {
        val reactions = binding.messageVg.findViewById<ReactionsViewGroup>(R.id.reactions)
        val emoji = EmojiView(this)
        with(emoji) {
            setEmoji(getRandomEmoji())
            setEmojiBackground()
            id = 0
            setOnClickListener {
                isSelected = !isSelected
                if (isSelected)
                    increaseCount()
                else
                    decreaseCount()
            }
        }
        reactions.addView(emoji)
        view.invalidate()
    }
}