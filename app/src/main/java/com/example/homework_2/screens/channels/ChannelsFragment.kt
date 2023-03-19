package com.example.homework_2.screens.channels

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.homework_2.Datasource
import com.example.homework_2.databinding.FragmentChannelsBinding
import com.google.android.material.tabs.TabLayout

class ChannelsFragment : Fragment() {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!
    private lateinit var streamAdapter: StreamAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        val context = requireContext()
        initRecyclerView(context, Datasource.getStreams())
        initTabView(context)

        return binding.root
    }

    private fun initTabView(context: Context) {
        val selectedTabIndex = if(Datasource.showSubscribed) 0 else 1
        binding.tlSelectChannel.getTabAt(selectedTabIndex)?.select()
        binding.tlSelectChannel.addOnTabSelectedListener(getTabListener(context))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(context: Context, streamsToShow: MutableList<Any>) {
        streamAdapter =
            StreamAdapter(streamsToShow, context, findNavController())
        binding.rvStreams.adapter = streamAdapter
        val rvLayoutManager = LinearLayoutManager(context)
        rvLayoutManager.generateDefaultLayoutParams()
        binding.rvStreams.layoutManager = rvLayoutManager
        (binding.rvStreams.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun getTabListener(context: Context) =
        object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Datasource.showSubscribed = tab?.position == 0
                initRecyclerView(context, Datasource.getStreams())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Datasource.renewStreamsOpenState()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        }
}