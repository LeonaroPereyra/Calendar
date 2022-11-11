package com.calendar.calendar.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String?= "",
    val lastName: String?= "",
    val curp: String?= "",
    val aka: String?= "",
    val image: String?= "",
    val imageBack: String?= "",
    val employeeName:String?= "",
    val active:String?= "",
    val noPrestamo:String?= "",
    )
