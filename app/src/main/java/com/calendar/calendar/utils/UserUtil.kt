package com.calendar.calendar.utils

import android.util.Log
import com.calendar.calendar.model.User
import com.google.firebase.firestore.*

class UserUtil {

    val myDb = FirebaseFirestore.getInstance()
    val collectioName = "User"

    fun saveUser(user: User) {
        myDb.collection(collectioName).add(user)
    }

}