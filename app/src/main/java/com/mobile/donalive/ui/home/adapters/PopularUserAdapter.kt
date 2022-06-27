package com.mobile.donalive.ui.home.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobile.donalive.databinding.ListItemPopularBinding
import com.mobile.donalive.ui.home.models.PopularDataModel

class PopularUserAdapter(var userList: ArrayList<PopularDataModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

//    private var userList = ArrayList<PopularDataModel>()

    lateinit var mContext: Context

    class CountryHolder(var viewBinding: ListItemPopularBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

//    init {
//        countryFilterList = userList
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemPopularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val sch = CountryHolder(binding)
        mContext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val userHolder = holder as CountryHolder
        Glide.with(mContext).load(userList[position].userImage).into(userHolder.viewBinding.ivImages)
//        countryHolder.viewBinding.selectCountryContainer.setBackgroundColor(Color.TRANSPARENT)
//
//        countryHolder.viewBinding.selectCountryText.setTextColor(Color.BLACK)
//        countryHolder.viewBinding.selectCountryText.text = countryFilterList[position].countryName
//
//        holder.itemView.setOnClickListener {
//            val intent = Intent(mContext, DetailsActivity::class.java)
//            intent.putExtra("passselectedcountry", countryFilterList[position].countryName)
//            mContext.startActivity(intent)
//            Log.d("Selected:", countryFilterList[position].countryName)
//        }
    }

    // in this list data come from new filter list
//    fun filterList(filteredList: ArrayList<PopularDataModel>) {
//        // set data main List
//        countryFilterList = filteredList
//        notifyDataSetChanged()
//    }

}