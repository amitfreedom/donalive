package com.mobile.donalive.ui.home.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabAdapter(var simpleTabList:List<Fragment>,fragmentManager: FragmentManager?,var tabTitle:Array<String>):FragmentPagerAdapter(fragmentManager!!) {
    override fun getCount(): Int {
        return simpleTabList.size
    }

    override fun getItem(position: Int): Fragment {

        return simpleTabList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (tabTitle.isEmpty()){
            null
        }else tabTitle[position]
    }
}