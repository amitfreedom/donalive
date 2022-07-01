package com.mobile.donalive

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.mobile.donalive.agora.openvcall.model.AGEventHandler
import com.mobile.donalive.agora.openvcall.model.CurrentUserSettings
import com.mobile.donalive.agora.openvcall.model.EngineConfig
import com.mobile.donalive.agora.openvcall.model.MyEngineEventHandler
import com.mobile.donalive.utils.AppPreferences
import dagger.hilt.android.HiltAndroidApp
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import org.slf4j.LoggerFactory

@HiltAndroidApp
class NoteApplication :Application() {
    lateinit var context: Context

    private val log = LoggerFactory.getLogger(this.javaClass)

    private var mRtcEngine: RtcEngine? = null
    private var mConfig: EngineConfig? = null
    private var mEventHandler: MyEngineEventHandler? = null
    private val mVideoSettings: CurrentUserSettings = CurrentUserSettings()



    fun rtcEngine(): RtcEngine? {
        return mRtcEngine
    }

    fun config(): EngineConfig? {
        return mConfig
    }

    fun addEventHandler(handler: AGEventHandler?) {
        mEventHandler!!.addEventHandler(handler)
    }

    fun remoteEventHandler(handler: AGEventHandler?) {
        mEventHandler!!.removeEventHandler(handler)
    }

    fun userSettings(): CurrentUserSettings? {
        return mVideoSettings
    }


    companion object {
        private lateinit var instace: NoteApplication

        lateinit var appPreference1: AppPreferences
        fun getAppPreference(): AppPreferences? {
            return appPreference1
        }

        fun the(): NoteApplication? {
            return instace
        }
    }

    override fun onCreate() {
        super.onCreate()
        instace = this
        context = applicationContext
        appPreference1 = AppPreferences.init(context, "piku")!!
        createRtcEngine()

    }


    private fun createRtcEngine() {
        val context = applicationContext
        val appId = context.getString(R.string.agora_app_id)
        if (TextUtils.isEmpty(appId)) {
            throw RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/")
        }
        mEventHandler = MyEngineEventHandler()
        mRtcEngine = try {
            // Creates an RtcEngine instance
            RtcEngine.create(context, appId, mEventHandler)
        } catch (e: Exception) {
            log.error(Log.getStackTraceString(e))
            throw RuntimeException(
                """
                     NEED TO check rtc sdk init fatal error
                     ${Log.getStackTraceString(e)}
                     """.trimIndent()
            )
        }

        /*
          Sets the channel profile of the Agora RtcEngine.
          The Agora RtcEngine differentiates channel profiles and applies different optimization
          algorithms accordingly. For example, it prioritizes smoothness and low latency for a
          video call, and prioritizes video quality for a video broadcast.
         */mRtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
        // Enables the video module.
        mRtcEngine?.enableVideo()
        /*
          Enables the onAudioVolumeIndication callback at a set time interval to report on which
          users are speaking and the speakers' volume.
          Once this method is enabled, the SDK returns the volume indication in the
          onAudioVolumeIndication callback at the set time interval, regardless of whether any user
          is speaking in the channel.
         */mRtcEngine?.enableAudioVolumeIndication(200, 3, false)
        mConfig = EngineConfig()
    }

}