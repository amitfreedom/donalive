package com.mobile.donalive.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mobile.donalive.R

class HostRegistrationFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_registration_form)
    }

    fun onBackPress(view: View) {
        onBackPressed()
    }
}