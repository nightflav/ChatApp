package com.example.homework_2.screens.contacts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework_2.R
import com.example.homework_2.utils.Status
import com.example.homework_2.models.UserProfile

class ContactsAdapter(private val context: Context) :
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    private var dataList: List<UserProfile> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_layout, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activeStatus = itemView.findViewById<CardView>(R.id.cv_is_active_status)
        private val profileImage = itemView.findViewById<ImageView>(R.id.iv_contacts_profile_image)
        private val name = itemView.findViewById<TextView>(R.id.tv_contact_card_name)
        private val email = itemView.findViewById<TextView>(R.id.tv_contact_card_email)

        fun bind(profile: UserProfile) {
            val isActiveStatusColor = when (profile.status) {
                Status.ACTIVE -> context.resources.getColor(R.color.online_status, context.theme)
                Status.OFFLINE -> context.resources.getColor(R.color.offline_status, context.theme)
                Status.IDLE -> context.resources.getColor(R.color.idle_status, context.theme)
                else -> throw IllegalStateException()
            }
            activeStatus.setCardBackgroundColor(isActiveStatusColor)
            Glide.with(profileImage).load(profile.avatarSource).into(profileImage)
            name.text = profile.fullName
            email.text = profile.email
        }
    }

    fun submitList(profiles: List<UserProfile>) {
        val diffUtil = DiffCallback(
            dataList,
            profiles
        )
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        dataList = profiles
        diffResult.dispatchUpdatesTo(this)
    }

    class DiffCallback(
        private val oldList: List<UserProfile>,
        private val newList: List<UserProfile>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val item1 = oldList[oldItemPosition]
            val item2 = newList[newItemPosition]
            return item1.email == item2.email
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val item1 = oldList[oldItemPosition]
            val item2 = newList[newItemPosition]
            return item1 == item2
        }
    }
}