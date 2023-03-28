package com.example.homework_2.screens.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.example.homework_2.R
import com.example.homework_2.databinding.FragmentProfileBinding
import com.example.homework_2.datasource.ProfilesDatasource

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val context = requireContext()
        val profile = ProfilesDatasource.getProfile()
        val profileImg = profile.tmpProfilePhoto
        val name = profile.fullName

        binding.ivProfileImage.setImageDrawable(
            profileImg?.let {
                AppCompatResources.getDrawable(
                    context,
                    it
                )
            }
        )

        binding.tvProfileName.text = name

        if (profile.isActive) {
            binding.tvProfileOnlineStatus.text = getString(R.string.online_is_active)
            binding.tvProfileOnlineStatus.setTextColor(
                resources.getColor(
                    R.color.online_status,
                    context.theme
                )
            )
        } else {
            binding.tvProfileOnlineStatus.text = getString(R.string.online_is_not_active)
            binding.tvProfileOnlineStatus.setTextColor(
                resources.getColor(
                    R.color.offline_status,
                    context.theme
                )
            )
        }

        binding.tvProfileMeetingStatus.text = profile.meetingStatus

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}