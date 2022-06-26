package com.mobile.donalive.ui.home.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
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
        simpleTabList.add(PopularFragment())
        simpleTabList.add(PopularFragment())
        simpleTabList.add(PopularFragment())
        simpleTabList.add(PopularFragment())
        simpleTabList.add(PopularFragment())

        binding.viewPager.adapter=TabAdapter(simpleTabList,childFragmentManager,tabTitles)
        binding.tabs.setupWithViewPager(binding.viewPager,true)
        binding.tabs.getTabAt(1)?.select()
        wrapTabIndicatorToTitle(binding.tabs,20,20);

    }

    fun wrapTabIndicatorToTitle(tabLayout: TabLayout, externalMargin: Int, internalMargin: Int) {
        val tabStrip = tabLayout.getChildAt(0)
        if (tabStrip is ViewGroup) {
            val childCount = tabStrip.childCount
            for (i in 0 until childCount) {
                val tabView = tabStrip.getChildAt(i)
                //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
                tabView.minimumWidth = 0
                // set padding to 0 for wrapping indicator as title
                tabView.setPadding(0, tabView.paddingTop, 0, tabView.paddingBottom)
                // setting custom margin between tabs
                if (tabView.layoutParams is MarginLayoutParams) {
                    val layoutParams = tabView.layoutParams as MarginLayoutParams
                    if (i == 0) {
                        // left
                        settingMargin(layoutParams, externalMargin, internalMargin)
                    } else if (i == childCount - 1) {
                        // right
                        settingMargin(layoutParams, internalMargin, externalMargin)
                    } else {
                        // internal
                        settingMargin(layoutParams, internalMargin, internalMargin)
                    }
                }
            }
            tabLayout.requestLayout()
        }
    }

    private fun settingMargin(layoutParams: MarginLayoutParams, start: Int, end: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.marginStart = start
            layoutParams.marginEnd = end
        } else {
            layoutParams.leftMargin = start
            layoutParams.rightMargin = end
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}