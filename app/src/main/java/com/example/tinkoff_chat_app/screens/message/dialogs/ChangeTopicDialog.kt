package com.example.tinkoff_chat_app.screens.message.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import com.example.tinkoff_chat_app.R

class ChangeTopicDialog : DialogFragment() {

    private lateinit var onSubmitButtonClickListener: (String) -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.change_message_topic_layout, null)
            val etTopic = view.findViewById<AutoCompleteTextView>(R.id.et_new_topic_name)
            builder.setView(view)
                .setPositiveButton(getString(R.string.change)) { _, _ ->
                    val newTopicName = etTopic.text.toString()
                    onSubmitButtonClickListener(newTopicName)
                    dialog?.cancel()
                }
                .setNegativeButton(getString(R.string.cancel_button)) { _, _ ->
                    dialog?.cancel()
                }

            builder.create()
        } ?: throw IllegalStateException()
    }

    companion object {
        fun newInstance(func: (String) -> Unit): ChangeTopicDialog {
            val f = ChangeTopicDialog()
            f.onSubmitButtonClickListener = func
            return f
        }
    }
}