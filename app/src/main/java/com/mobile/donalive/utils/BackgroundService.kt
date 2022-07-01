package com.mobile.donalive.utils

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.mobile.donalive.agora.openvcall.ui.CallActivity.liveType
import com.mobile.donalive.utils.CommonUtils.Companion.getUserId
import java.util.HashMap

class BackgroundService : Service() {

    var count: CountDownTimer? = null

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val ref = firebaseDatabase.reference.child("userInfo")

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        count = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i("onTick: ", "" + millisUntilFinished / 1000 + "")
                if (!Singleton.isStop) {
                    count?.cancel()
                }
            }

            override fun onFinish() {
                Log.i("onTick: ", Singleton.isStop.toString())
                if (Singleton.isStop) {
                    Singleton.isStopLive = true
                    setLiveStreamOffline()
                    Log.i("onTick: ", "finish")
                } else {

                }
            }
        }.start()
        return START_STICKY;
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)


    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun setLiveStreamOffline() {
        val data = HashMap<String, Boolean>()
        data["live"] = false
        ref.child(getUserId()).child(liveType).child(getUserId()).child("hostLiveInfo")
            .setValue(data)
    }




}