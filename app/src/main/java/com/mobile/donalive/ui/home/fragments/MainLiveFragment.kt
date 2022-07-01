package com.mobile.donalive.ui.home.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.mobile.donalive.MainActivity
import com.mobile.donalive.NoteApplication
import com.mobile.donalive.R
import com.mobile.donalive.agora.openvcall.ui.CallActivity
import com.mobile.donalive.databinding.FragmentMainLiveBinding
import com.mobile.donalive.utils.AppConstants
import com.mobile.donalive.utils.CommonUtils
import com.mobile.donalive.utils.Singleton
import com.mobile.donalive.utils.Singleton.Companion.isStopLive
import com.mobile.donalive.viewmodels.VM


class MainLiveFragment : Fragment() {
    private var _binding:FragmentMainLiveBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainLiveBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isStopLive = false
        clicks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun clicks() {

        binding.btnSimple.setOnClickListener {
            hitGenerateToken(CommonUtils.getUserId(), "singleLive", "host")
        }
        binding.btnMultiLive.setOnClickListener {
            hitGenerateToken(CommonUtils.getUserId(), "multiLive", "host")
        }
        binding.btnPK.setOnClickListener {
            hitGenerateToken(CommonUtils.getUserId(), "pkLive", "host")
        }


        binding.btnStart.setOnClickListener {
            binding.btnAni.visibility = View.VISIBLE
            var animation = AnimationUtils.loadAnimation(activity, R.anim.right_to_left)
            binding.btnAni.startAnimation(animation)
        }

    }


    private fun hitGenerateToken(userId: String, liveType: String, liveStatus: String) {
        var data = HashMap<String, String>()
        data["userId"] = "12345"
        data["channelName"] = "userId123"
        data["liveType"] = liveType
        data["liveStatus"] = liveStatus
        VM().liveMultiLiveToken(activity!!, data).observe(viewLifecycleOwner, Observer {
            when (it.success) {
                "1" ->{
                    Toast.makeText(activity!!, "" + it.message, Toast.LENGTH_SHORT).show()

                    dialog()
                }

//                "1" -> {
//                    Toast.makeText(activity!!, "" + it.message, Toast.LENGTH_SHORT).show()
//
//
//                    var i: Intent? = null
//
//                    i = when (liveType) {
//                        "singleLive" -> {
//                            Singleton.liveType = ""
//                            Intent(activity, CallActivity::class.java)
//                        }
//                        "multiLive" -> {
//                            Singleton.liveType = ""
//                            Intent(activity, CallActivity::class.java)
//                        }
//                        else -> {
//                            Singleton.liveType = "PK"
//                            Intent(activity, CallActivity::class.java)
//                        }
//                    }
//
//                    i.putExtra("channelName", it.token.channelName)
//                    i.putExtra("userId", it.token.userId)
//                    i.putExtra("liveType", it.token.liveType)
//                    i.putExtra("liveStatus", "hostLive")
//                    i.putExtra("token", it.token.token)
//                    i.putExtra("tokenRTM", it.token.token)
//                    i.putExtra("name", it.token.name)
//                    i.putExtra("image", it.token.image)
//                    i.putExtra("count", it.token.count)
//                    startActivity(i)
//                    activity!!.finish()
//                }
//                else -> {
//                    Toast.makeText(activity, "" + it.message, Toast.LENGTH_SHORT).show()
//                }
            }
        })

    }

    private fun dialog() {
        val builder = AlertDialog.Builder(activity!!)
        //set title for alert dialog
        //set message for alert dialog
        builder.setMessage("host live implemented successfully please contact to agora developer to activate this key")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("ok"){dialogInterface, which ->
            Toast.makeText(activity,"thank you",Toast.LENGTH_LONG).show()
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    override fun onResume() {
        super.onResume()
        VM().getCurrentCoin(activity!!, CommonUtils.getUserId()).observe(this,
            Observer {
                when (it.success) {
                    "1" -> {
                        NoteApplication.appPreference1.saveString(AppConstants.CURRENT_DIAMON, it.dimaond)
                    }
                    else -> {

                    }
                }
            })
    }

}