package com.example.homework_2.screens.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework_2.Datasource
import com.example.homework_2.R
import com.example.homework_2.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding: FragmentContactsBinding
    get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        val context = requireContext()
        val contactsList = Datasource.getContacts()

        //temporary. will be removed later
        contactsList[0].tmpProfilePhoto = ResourcesCompat.getDrawable(
            resources,
            R.drawable.tmp_agatha_christie,
            context.theme
        )
        contactsList[1].tmpProfilePhoto = ResourcesCompat.getDrawable(
            resources,
            R.drawable.tmp_arthur_conan_doyle,
            context.theme
        )

        val contactsAdapter = ContactsAdapter(context, contactsList)
        binding.rvContacts.layoutManager = LinearLayoutManager(context)
        binding.rvContacts.adapter = contactsAdapter

        return binding.root
    }
}