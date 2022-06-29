package com.mobile.donalive.ui.profile.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobile.donalive.databinding.ListItemChatBinding
import com.mobile.donalive.databinding.ListItemLevelBinding
import com.mobile.donalive.databinding.ListItemPopularBinding
import com.mobile.donalive.databinding.ListLiveGiftHistoryBinding
import com.mobile.donalive.ui.home.models.PopularDataModel

class LiveHistoryAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

//    private var userList = ArrayList<PopularDataModel>()

    lateinit var mContext: Context

    class CountryHolder(var viewBinding: ListLiveGiftHistoryBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListLiveGiftHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val sch = CountryHolder(binding)
        mContext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return 15
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val userHolder = holder as CountryHolder
//        Glide.with(mContext).load(userList[position].userImage).into(userHolder.viewBinding.ivImages)

    }



}