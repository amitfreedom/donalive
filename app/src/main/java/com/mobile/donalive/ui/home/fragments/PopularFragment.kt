package com.mobile.donalive.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.donalive.R
import com.mobile.donalive.databinding.FragmentPopularBinding
import com.mobile.donalive.ui.home.adapters.ImageSlideAdapter


class PopularFragment : Fragment() {

    private var _binding:FragmentPopularBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPopularBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val images: ArrayList<Int> = ArrayList()
        images.add(R.drawable.pk)
        images.add(R.drawable.pk2)

        binding.viewpager.adapter=ImageSlideAdapter(activity!!,images)
        binding.indicator.setViewPager(binding.viewpager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}