package com.calendar.calendar.view

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import java.math.BigInteger
import java.security.MessageDigest

abstract class BaseActivity : AppCompatActivity() {
    lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Loading")
        mProgressDialog.setCancelable(false)
        mProgressDialog.isIndeterminate = true
    }

    fun validateTime() {
        if (!isAutoTimeEnabled(this) ||
            !isAutoTimeZoneEnabled(this)
        ) {
            onCreateDialog("Poner Hora en Automatico")
        }
        if (isMockLocationEnabled(this) == true) {
            onCreateDialog("Apagar Fake Location")
        }
    }

    fun isAutoTimeEnabled(activity: Activity) =
        Settings.Global.getInt(activity.contentResolver, Settings.Global.AUTO_TIME) == 1

    fun isAutoTimeZoneEnabled(activity: Activity) =
        Settings.Global.getInt(activity.contentResolver, Settings.Global.AUTO_TIME_ZONE) == 1

    fun onCreateDialog(text: String): Dialog {
        return this?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(text).setCancelable(false)
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun isMockLocationEnabled(context: Context): Boolean? {
        return Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ALLOW_MOCK_LOCATION
        ) != "0"
    }

    fun showProgressDialog() {
        if (!mProgressDialog.isShowing) {
            mProgressDialog.show()
        }
    }

    fun dismissProgressDialog() {
        if (mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
    }
}