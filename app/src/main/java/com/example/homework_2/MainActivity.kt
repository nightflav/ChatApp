package com.example.homework_2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.homework_2.databinding.ActivityMainBinding
import com.example.homework_2.models.Reaction
import com.example.homework_2.models.SingleMessage
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val msgAdapter by lazy {
        MessageAdapter(
            getReaction = { msgId -> getReaction(msgId) },
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.rvChat.adapter = msgAdapter
        val rvLayoutManager = LinearLayoutManager(this)
        rvLayoutManager.generateDefaultLayoutParams()
        binding.rvChat.layoutManager = rvLayoutManager
        (binding.rvChat.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        setSendButtonChange()
        setSendButtonOnClickListener(msgAdapter)
    }

    private fun setMessageOnClickListener(
        msgAdapter: MessageAdapter,
        reaction: Reaction,
        msgId: String
    ) {
        Datasource.addReaction(reaction, msgId)
        Log.d("mmytag", "${Datasource.getReactions(msgId)}")
        msgAdapter.notifyItemChanged(Datasource.getMessages().map { it.message_id }.indexOf(msgId))
//        binding.rvChat.visibility = View.GONE
//        binding.rvChat.visibility = View.VISIBLE
    }

    private fun getReaction(msgId: String) {
        val dialogLayout =
            layoutInflater.inflate(R.layout.bsd_select_emoji_layout, LinearLayoutCompat(this))
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val rvEmojis = dialogLayout.findViewById<RecyclerView>(R.id.rv_emoji_bsd)
        val emojiAdapter = EmojiAdapter(Datasource.getEmojis()) {
            setMessageOnClickListener(
                msgAdapter = msgAdapter,
                reaction = Reaction(Datasource.emojiSetNCS[Datasource.getEmojis().indexOf(it)], 1, true),
                msgId = msgId
            )
            dialog.cancel()
        }
        dialog.setContentView(dialogLayout)
        rvEmojis.layoutManager = GridLayoutManager(this, 7)
        rvEmojis.adapter = emojiAdapter
        dialog.behavior.maxHeight = 700f.dp(this).toInt()
        dialog.show()
    }

    private fun setSendButtonOnClickListener(adapter: MessageAdapter) {
        binding.btnSend.setOnClickListener {
            if (binding.etMessage.text?.isNotEmpty() == true) {
                val newMsg = SingleMessage(
                    msg = binding.etMessage.text.toString(),
                    reactions = mutableListOf(),
                    user_id = "user_1",
                    senderName = "Yaroslav",
                    message_id = Datasource.getMessages().size.toString() + 1
                )
                Datasource.addMessage(newMsg)
                binding.etMessage.text!!.clear()
                adapter.notifyItemInserted(Datasource.getMessages().size)
                binding.rvChat.scrollToPosition(Datasource.getMessages().lastIndex)
            }
        }
    }

    private fun setSendButtonChange() {
        binding.etMessage.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (text.isEmpty()) {
                    binding.btnSend.setImageDrawable(
                        AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_add)
                    )
                } else {
                    binding.btnSend.setImageDrawable(
                        AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_send)
                    )
                }
            }
        }
    }
}