package com.mobile.donalive.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.mobile.donalive.R
import com.mobile.donalive.databinding.FragmentPopularBinding
import com.mobile.donalive.ui.home.adapters.ImageSlideAdapter
import com.mobile.donalive.ui.home.adapters.PopularUserAdapter
import com.mobile.donalive.ui.home.models.PopularDataModel


class PopularFragment : Fragment() {

    private var _binding: FragmentPopularBinding?=null
    private val binding get() = _binding!!
    private lateinit var adapter: PopularUserAdapter
    private val userList = java.util.ArrayList<PopularDataModel>()


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
        imageSlider()
        addUserImage()
        setUserAdapter()

    }

    private fun addUserImage() {
        userList.add(PopularDataModel("https://i.pinimg.com/736x/08/87/73/088773df0ebba35e4159de36dfd89953.jpg"))
        userList.add(PopularDataModel("https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8dXNlciUyMHByb2ZpbGV8ZW58MHx8MHx8&w=1000&q=80"))
        userList.add(PopularDataModel("https://preview.keenthemes.com/metronic-v4/theme/assets/pages/media/profile/profile_user.jpg"))
        userList.add(PopularDataModel("https://wac-cdn.atlassian.com/dam/jcr:ba03a215-2f45-40f5-8540-b2015223c918/Max-R_Headshot%20(1).jpg?cdnVersion=402"))
        userList.add(PopularDataModel("https://www.niemanlab.org/images/Greg-Emerson-edit-2.jpg"))
        userList.add(PopularDataModel("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSTjdUF61LZHXczY4oBWQ6gi-_1sty5xl2VK0DADO1tOQCwVI9Yz_VkuQECc1r0upXMMes&usqp=CAU"))
        userList.add(PopularDataModel("https://images.unsplash.com/photo-1524666041070-9d87656c25bb?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8bWFsZXxlbnwwfHwwfHw%3D&w=1000&q=80"))
        userList.add(PopularDataModel("https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8dXNlcnxlbnwwfHwwfHw%3D&w=1000&q=80"))
        userList.add(PopularDataModel("https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8dXNlciUyMHByb2ZpbGV8ZW58MHx8MHx8&w=1000&q=80"))
        userList.add(PopularDataModel("https://preview.keenthemes.com/metronic-v4/theme/assets/pages/media/profile/profile_user.jpg"))
        userList.add(PopularDataModel("https://wac-cdn.atlassian.com/dam/jcr:ba03a215-2f45-40f5-8540-b2015223c918/Max-R_Headshot%20(1).jpg?cdnVersion=402"))
        userList.add(PopularDataModel("https://preview.keenthemes.com/metronic-v4/theme/assets/pages/media/profile/profile_user.jpg"))
        userList.add(PopularDataModel("https://wac-cdn.atlassian.com/dam/jcr:ba03a215-2f45-40f5-8540-b2015223c918/Max-R_Headshot%20(1).jpg?cdnVersion=402"))
        userList.add(PopularDataModel("https://www.niemanlab.org/images/Greg-Emerson-edit-2.jpg"))
    }

    private fun setUserAdapter() {
        var mLayoutManager = GridLayoutManager(activity, 3)
        adapter = PopularUserAdapter(userList)
        binding.recyclerView.adapter = adapter
        mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 -> 2
                    else -> 1
                }
            }
        }
        binding.recyclerView.layoutManager=mLayoutManager
    }

    private fun imageSlider() {
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