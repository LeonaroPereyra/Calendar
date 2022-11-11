package com.calendar.calendar.model

data class Prestamo(
    val curp: String,
    val name: String,
    val fechaInicio: String,
    val fechaFin: String,
    val interesDiario: String,
    val montoInicial: String,
    val montoFinal: String,
    val latitude: String,
    val longitude: String,
    val employeName: String,
    val isActived: String,
    val dateRegisterPay: String,
    val saldo: String,
    val totalAbonos: String,
    val noAbonos : String,
    val noPrestamo:String
) : Comparable<Prestamo>{
    override fun compareTo(other: Prestamo): Int {
        return this.isActived.compareTo(other.isActived)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Prestamo

        if (curp != other.curp) return false
        if (noPrestamo != other.noPrestamo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = curp.hashCode()
        result = 31 * result + noPrestamo.hashCode()
        return result
    }


}