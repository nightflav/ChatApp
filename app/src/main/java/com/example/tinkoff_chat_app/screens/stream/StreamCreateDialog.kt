package com.example.tinkoff_chat_app.screens.stream

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.tinkoff_chat_app.R
import com.google.android.material.switchmaterial.SwitchMaterial

class StreamCreateDialog : DialogFragment() {

    private lateinit var onSubmitButtonClickListener: (String, String?, Boolean) -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.create_new_stream_dialog_layout, null)
            val etName = view.findViewById<EditText>(R.id.et_stream_name_dialog)
            val etDisc = view.findViewById<EditText>(R.id.et_stream_description_dialog)
            val announceSwitch = view.findViewById<SwitchMaterial>(R.id.switch_announce)
            builder.setView(view)
                .setPositiveButton(getString(R.string.submit_button)) { _, _ ->
                    val streamName = etName.text.toString()
                    val streamDisc = etDisc.text.toString().ifEmpty { null }
                    val announce = announceSwitch.isSelected
                    onSubmitButtonClickListener(streamName, streamDisc, announce)
                    dialog?.cancel()
                }
                .setNegativeButton(getString(R.string.cancel_button)) { _, _ ->
                    dialog?.cancel()
                }

            builder.create()
        } ?: throw IllegalStateException()
    }

    companion object {
        fun newInstance(func: (String, String?, Boolean) -> Unit): StreamCreateDialog {
            val f = StreamCreateDialog()
            f.onSubmitButtonClickListener = func
            return f
        }
    }
}