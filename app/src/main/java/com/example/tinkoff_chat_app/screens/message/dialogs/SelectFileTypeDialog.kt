package com.example.tinkoff_chat_app.screens.message.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.tinkoff_chat_app.R

class SelectFileTypeDialog : DialogFragment() {

    private lateinit var onImageTypeClickListener: () -> Unit
    private lateinit var onDocumentTypeListener: () -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = Dialog(requireContext())
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.select_type_of_file_dialog, null)
            view.findViewById<Button>(R.id.btn_photo_type).setOnClickListener {
                onImageTypeClickListener()
                dialog!!.cancel()
            }
            view.findViewById<Button>(R.id.btn_docs_type).setOnClickListener {
                onDocumentTypeListener()
                dialog!!.cancel()
            }
            builder.setContentView(view)
            builder
        } ?: throw IllegalStateException()
    }

    companion object {
        fun newInstance(imageFunc: () -> Unit, docFunc: () -> Unit): SelectFileTypeDialog {
            val f = SelectFileTypeDialog()
            f.onImageTypeClickListener = imageFunc
            f.onDocumentTypeListener = docFunc
            return f
        }
    }
}