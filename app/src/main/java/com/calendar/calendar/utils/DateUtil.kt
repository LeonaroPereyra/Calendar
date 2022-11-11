package com.calendar.calendar.utils


import android.os.Build
import androidx.annotation.RequiresApi
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class DateUtil {

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(Date())
    }
    fun getCurrentDateFormat(): String {
        val sdf = SimpleDateFormat("dd_MM_yyyy")
        return sdf.format(Date())
    }

    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        return sdf.format(Date())
    }

    fun getTimeStamp(): String {
        var tsLong = System.currentTimeMillis() / 1000
        return tsLong.toString()
    }

    fun getDiasTrans(fechaInicio: String): Int {
        var dias = 0
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date1 = sdf.parse(getCurrentDate())
        val dateInit = sdf.parse(fechaInicio)
        if (dateInit != null) {
            dias = (( date1.time - dateInit.time) / 86400000).toInt()
        }
        return dias
    }

    fun getDias(fechaFin: String): Int {
        var dias = 0
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date1 = sdf.parse(getCurrentDate())
        val dateFin = sdf.parse(fechaFin)
        if (dateFin != null) {
            dias = ((dateFin.time - date1.time) / 86400000).toInt()
        }
        return dias
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sumDias(dias: Long): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date ()
        calendar.add(Calendar.DAY_OF_YEAR, dias.toInt() )
        return SimpleDateFormat("dd/MM/yyyy").format(calendar.time)
        //return calendar.time
    }

    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

}