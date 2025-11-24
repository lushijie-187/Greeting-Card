package com.example.greetingcard

import android.content.Context
import android.content.SharedPreferences

object LoginManager {

    private const val PREFS_NAME = "LoginPrefs"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // 保存登录状态
    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply() // apply() 是异步保存，更高效
    }

    // 读取登录状态
    fun isLoggedIn(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }
}
