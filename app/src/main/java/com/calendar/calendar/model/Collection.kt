package com.calendar.calendar.model

import java.lang.ref.Reference

class Collection {
    object User {
        var NAME = "Name"
        var LASTNAME = "LastName"
        var CURP = "CURP"
        var ALIAS = "Alias"
        var IMAGEFONT = "ImageFont"
        var IMAGEBACK = "ImageBack"
        var USER = "Users"
        var ACTIVE = "active"
        var NOPRESTAMO = "noPrestamo"
        var EMPLOYE = "employeName"
        var STATUSUSER: MutableList<String> = mutableListOf("DISPONIBLE", "NO DISPONIBLE")
    }

    object Prestamo {
        var CURP = "CURP"
        var MONTOINICIAL = "montoInicial"
        var MONTOFINAL = "montoFinal"
        var FECHAINIT = "dateInit"
        var FECHAEND = "dateEnd"
        var ACTIVE = "active"
        var INTERESDIARIO = "interesDiario"
        var REGISTROCREDITO = "Registro"
        var REGISTROPAGO = "RegistroPago"
        var LATITUDE = "latitude"
        var LONGITUDE = "longitude"
        var USERNAME = "userName"
        var USERNAMECOMPLETE = "userName"
        var EMPLOYE = "employeName"
        var SALDO = "saldo"
        var DATEPAY = "fechaPago"
        var NOABONOS = "numeroAbono"
        var TOTALABONOS = "totalABonos"
        var NOPRESTAMO = "noPrestamo"
        var STATUSPRESTAMO: MutableList<String> = mutableListOf("NORMAL", "SALDADO", "ATRASADO")

    }

    object Employe {
        var USERNAME = "userName"
        var TYPE = "type"
        var ACTIVE = "active"
        var DOCREF = "Employe"
        var LATITUDE = "latitude"
        var LONGITUDE = "longitude"
        var STATUS: MutableList<String> = mutableListOf("ACTIVO", "INACTIVO")
        var TYPEUSER: MutableList<String> = mutableListOf("ADMIN", "USER")
    }
}