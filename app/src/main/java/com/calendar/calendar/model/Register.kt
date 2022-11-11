package com.calendar.calendar.model

data class Register(
    val fecha: Long,
    val montoPagado: String,
    val montoPagar: String,
    val noTarjeta: String,
    val pagado: Boolean,
    val position: Position
)
