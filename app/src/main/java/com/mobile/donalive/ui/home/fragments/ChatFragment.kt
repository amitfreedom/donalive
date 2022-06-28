package com.mobile.donalive.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mobile.donalive.databinding.FragmentChatBinding
import com.mobile.donalive.ui.home.adapters.ChatAdapter
import com.mobile.donalive.ui.home.adapters.PopularUserAdapter
import com.mobile.donalive.ui.home.models.PopularDataModel

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding?=null
    private val binding get() = _binding!!
    private lateinit var adapter: ChatAdapter

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
        adapter = ChatAdapter()
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


}