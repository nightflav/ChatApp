package com.example.homework_2.screens.contacts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.R
import com.example.homework_2.models.UserProfile

class ContactsAdapter(private val context: Context, private val contactsList: List<UserProfile>) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_layout, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int = contactsList.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactsList[position])
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activeStatus = itemView.findViewById<CardView>(R.id.cv_is_active_status)
        private val profileImage = itemView.findViewById<ImageView>(R.id.iv_contacts_profile_image)
        private val name = itemView.findViewById<TextView>(R.id.tv_contact_card_name)
        private val email = itemView.findViewById<TextView>(R.id.tv_contact_card_email)

        fun bind(profile: UserProfile) {
            val isActiveStatusColor = if(profile.isActive)
                context.resources.getColor(R.color.online_status, context.theme)
            else
                context.resources.getColor(R.color.offline_status, context.theme)
            activeStatus.setCardBackgroundColor(isActiveStatusColor)
            profileImage.setImageDrawable(profile.tmpProfilePhoto)
            name.text = profile.fullName
            email.text = profile.email
        }
    }
}