package com.mobile.donalive.ui.home.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mobile.donalive.databinding.FragmentHomeBinding
import com.mobile.donalive.ui.home.adapters.TabAdapter


class HomeFragment : Fragment() {

    private var _binding:FragmentHomeBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabTitles = arrayOf("Freshers","Popular","Spotlight","Party","PK Matches")
        val simpleTabList : MutableList<Fragment> = ArrayList()
        simpleTabList.add(MainLiveFragment())
        simpleTabList.add(PopularFragment())
        simpleTabList.add(MainLiveFragment())
        simpleTabList.add(MainLiveFragment())
        simpleTabList.add(MainLiveFragment())

        binding.viewPager.adapter=TabAdapter(simpleTabList,childFragmentManager,tabTitles)
        binding.tabs.setupWithViewPager(binding.viewPager,true)
        binding.tabs.getTabAt(1)?.select()


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}