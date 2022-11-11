package com.calendar.calendar.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.calendar.calendar.model.Employee
import com.calendar.calendar.model.User

class SessionManager {

    var pref: SharedPreferences
    var edior: SharedPreferences.Editor
    var context: Context
    var PRIVATE_MODE: Int = 0

    constructor(context: Context) {

        this.context = context
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        edior = pref.edit()
    }

    companion object {

        val PREF_NAME: String = "calendar"
        val IS_LOGIN: String = "isLogin"
        val KEY_NAME: String = "name"
        val KEY_TYPE: String = "type"

    }

    fun createLoginSession(userName: String, type: String) {
        edior.putBoolean(IS_LOGIN, true)
        edior.putString(KEY_NAME, userName)
        edior.putString(KEY_TYPE, type)
        edior.commit()
    }

  fun getEmployee(): Employee
  =Employee (userName = pref.getString(KEY_NAME, null).toString(),
      type = pref.getString(KEY_TYPE, null).toString()
    )


    fun LogoutUser() {
        edior.clear()
        edior.commit()
    }

    fun isLoggedIn(): Boolean {
        return pref.getBoolean(IS_LOGIN, false)
    }

}