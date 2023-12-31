package com.example.tinkoff_chat_app.screens.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.tinkoff_chat_app.R
import com.example.tinkoff_chat_app.databinding.FragmentProfileBinding
import com.example.tinkoff_chat_app.di.ViewModelFactory
import com.example.tinkoff_chat_app.models.ui_models.UserProfile
import com.example.tinkoff_chat_app.utils.Status
import com.example.tinkoff_chat_app.utils.getAppComponent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

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
            is ProfileScreenState.Error -> {
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
                    getString(R.string.online_is_not_active),
                    R.color.offline_status
                )
            }
            else -> {
                Pair(getString(R.string.online_is_idle), R.color.idle_status)
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