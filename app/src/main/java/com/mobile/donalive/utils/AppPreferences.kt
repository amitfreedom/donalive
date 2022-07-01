package com.mobile.donalive.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class AppPreferences private constructor(
    context: Context,
    DATABASE_NAME: String
) {
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE)

    fun saveString(key: String?, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun getString(key: String?): String? {
        return sharedPreferences.getString(key, "")
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }

    fun saveUserDetails(key: String?, registerModelClass: Any?) {
        val gson = Gson()
        val editor = sharedPreferences.edit()
        editor.putString(key, gson.toJson(registerModelClass))
        editor.apply()
    }

    fun <T> getUserDetails(key: String?, type: Class<T>?): T {
        val gson = Gson()
        return gson.fromJson(sharedPreferences.getString(key, ""), type)
    }

    fun setSettings(key: String?, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getSettings(key: String?): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    val isUserFirstTime: Boolean
        get() = sharedPreferences.getBoolean("isUserFirstTime", true)

    fun setUserLoginTime() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isUserFirstTime", false)
        editor.apply()
    }

    fun login(context: Context, connected: Boolean) {
        sharedPreferences = context.getSharedPreferences("piku", 0)
        val editor = sharedPreferences.edit()
        editor.putBoolean("connected", connected)
        editor.commit()
    }

    fun isLogin(context: Context?): Boolean {
        return sharedPreferences.getBoolean("connected", false)
    }

    companion object {
        private var appPreference: AppPreferences? = null
        fun init(context: Context, DATABASE_NAME: String): AppPreferences? {
            if (appPreference == null) {
                appPreference = AppPreferences(context, DATABASE_NAME)
            }
            return appPreference
        }
    }

}