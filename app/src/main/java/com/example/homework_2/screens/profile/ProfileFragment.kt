package com.example.homework_2.screens.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.homework_2.R
import com.example.homework_2.databinding.FragmentProfileBinding
import com.example.homework_2.models.UserProfile
import com.example.homework_2.utils.Status
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            viewModel.profileChannel.send(ProfileIntents.InitProfile)
        }
        viewModel.screenState
            .flowWithLifecycle(lifecycle)
            .onEach(::render)
            .launchIn(lifecycleScope)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: ProfileScreenState) {
        when (state) {
            ProfileScreenState.Error -> {
                binding.apply {
                    tvProfileError.isVisible = true
                    cdHelperProfileScreen.isVisible = false
                    tvProfileName.isVisible = false
                    tvProfileOnlineStatus.isVisible = false
                    shimmerProfile.isVisible = false
                }
            }
            is ProfileScreenState.Success -> {
                binding.apply {
                    tvProfileError.isVisible = false
                    shimmerProfile.isVisible = false
                }
                showProfile(state.profile)
            }
            ProfileScreenState.Init -> {}
            ProfileScreenState.Loading -> {
                binding.apply {
                    tvProfileError.isVisible = false
                    cdHelperProfileScreen.isVisible = false
                    tvProfileName.isVisible = false
                    tvProfileOnlineStatus.isVisible = false
                    shimmerProfile.isVisible = true
                }
            }
        }
    }

    private fun showProfile(profile: UserProfile) {

        binding.apply {
            cdHelperProfileScreen.isVisible = true
            tvProfileName.isVisible = true
            tvProfileOnlineStatus.isVisible = true
        }

        Glide.with(this).load(profile.avatarSource).into(binding.ivProfileImage)
        binding.tvProfileName.text = profile.fullName
        val textAndColor = when (profile.status) {
            Status.ACTIVE -> {
                Pair(
                    getString(R.string.online_is_active),
                    R.color.online_status
                )
            }
            Status.OFFLINE -> {
                Pair(
                    getString(R.string.online_is_active),
                    R.color.online_status
                )
            }
            else -> {
                Pair(getString(R.string.online_is_active), R.color.online_status)
            }
        }
        binding.tvProfileOnlineStatus.text = textAndColor.first
        binding.tvProfileOnlineStatus.setTextColor(
            resources.getColor(
                textAndColor.second,
                context?.theme
            )
        )
    }
}