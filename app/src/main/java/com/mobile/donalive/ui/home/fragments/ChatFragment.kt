package com.mobile.donalive.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.ar.core.Config
import com.mobile.donalive.R
import com.mobile.donalive.databinding.FragmentChatBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory

class ChatFragment : Fragment() {

    private var _binding:FragmentChatBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChat()
    }

    private fun setupChat() {
        // Step 1 - Set up the OfflinePlugin for offline storage
//        val offlinePluginFactory = StreamOfflinePluginFactory(
//            config = Config(
//                backgroundSyncEnabled = true,
//                userPresence = true,
//                persistenceEnabled = true,
//                uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING,
//            ),
//
//        )

        // Step 2 - Set up the client for API calls with the plugin for offline storage
        val client = ChatClient.Builder("wz4qu92srhm5", activity!!)
//            .withPlugin(offlinePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()

        // Step 3 - Authenticate and connect the user
        val user = User(
            id = "tutorial-droid",
            name = "Tutorial Droid",
            image = "https://bit.ly/2TIt8NR"
        )
        client.connectUser(
            user = user,
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiRzRVVVZMb0RSRmNtaUtGUEdiQm16ODNOWWVrMiIsImlhdCI6MTY1NjQwODE1OH0.8GgPLPwN7N5pDQ6Hrgkx1ItKih6C-4wEVOi4T2u_Vls"
        ).enqueue()

        // Step 4 - Set the channel list filter and order
        // This can be read as requiring only channels whose "type" is "messaging" AND
        // whose "members" include our "user.id"
        val filter = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(user.id))
        )
        val viewModelFactory = ChannelListViewModelFactory(filter, ChannelListViewModel.DEFAULT_SORT)
        val viewModel: ChannelListViewModel by viewModels { viewModelFactory }

        // Step 5 - Connect the ChannelListViewModel to the ChannelListView, loose
        //          coupling makes it easy to customize
        viewModel.bindView(binding.channelListView, this)
        binding.channelListView.setChannelItemClickListener { channel ->
            // TODO - start channel activity
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


}