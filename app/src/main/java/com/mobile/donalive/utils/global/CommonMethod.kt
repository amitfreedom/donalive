package com.mobile.donalive.utils.global

import android.content.Context
import android.widget.Toast

object CommonMethod {
    fun showMessage(context: Context?,message:String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}