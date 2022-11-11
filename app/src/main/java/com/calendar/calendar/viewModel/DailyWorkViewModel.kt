package com.calendar.calendar.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Collection.Prestamo.STATUSPRESTAMO
import com.calendar.calendar.model.Prestamo
import com.calendar.calendar.utils.DateUtil
import com.calendar.calendar.viewModel.action.DailyAction
import com.calendar.calendar.viewModel.action.RegisterPayAction
import com.google.firebase.firestore.FirebaseFirestore

class DailyWorkViewModel : ViewModel() {
    private var onAction: MutableLiveData<DailyAction> = MutableLiveData<DailyAction>()
    private var db: FirebaseFirestore? = null
    private var listPrestamo: MutableList<Prestamo> = mutableListOf<Prestamo>()
    private var lstEmployee: MutableList<String> = mutableListOf<String>()
    private var employeeFilter: String? = ""
    private var prestamoFilter: String? = ""
    private var fechaBuscar: String = ""
    private var userName: String = ""
    private var list: MutableList<Float> = mutableListOf()


    fun getEmployee() {
        db = FirebaseFirestore.getInstance()
        db?.collection(Collection.Employe.DOCREF)?.addSnapshotListener { snapshots, e ->
            if (snapshots != null) {
                for (document in snapshots) {
                    lstEmployee.add(
                        document.data[Collection.Employe.USERNAME].toString()
                    )
                }
                lstEmployee.add(0, "Todos los Empleados")
                onAction.postValue(DailyAction.SetEmployeeList(lstEmployee.toHashSet()))
            }
        }
    }

    private fun setUpWithFilter(efilter: String?, statusFilter: String?) {
        employeeFilter = efilter
        prestamoFilter = statusFilter
        if (employeeFilter?.isEmpty() == true && prestamoFilter?.isEmpty() == true) {
            getAllPrestamo()
        } else if (employeeFilter?.isNotEmpty() == true || prestamoFilter?.isNotEmpty() == true) {

        }
    }


    fun getAllPrestamo() {
        listPrestamo= mutableListOf()
        var totalPrestado = 0F
        var totalInteres = 0F
        var totalGanancia = 0F
        db = FirebaseFirestore.getInstance()
        db?.collection(Collection.Prestamo.REGISTROCREDITO)?.addSnapshotListener { snapshots, e ->
            if (snapshots != null) {
                for (document in snapshots) {
                    totalPrestado += document.data[Collection.Prestamo.MONTOINICIAL].toString()
                        .toFloat()
                    totalInteres += document.data[Collection.Prestamo.MONTOFINAL].toString()
                        .toFloat()
                    totalGanancia += document.data[Collection.Prestamo.MONTOFINAL].toString()
                        .toFloat().minus(
                            document.data[Collection.Prestamo.MONTOINICIAL].toString()
                                .toFloat()
                        )
                    listPrestamo.add(
                        Prestamo(
                            fechaInicio = document.data[Collection.Prestamo.FECHAINIT].toString(),
                            fechaFin = document.data[Collection.Prestamo.FECHAEND].toString(),
                            curp = document.data[Collection.Prestamo.CURP].toString(),
                            name = document.data[Collection.Prestamo.USERNAME].toString(),
                            interesDiario = document.data[Collection.Prestamo.INTERESDIARIO].toString(),
                            montoInicial = document.data[Collection.Prestamo.MONTOINICIAL].toString(),
                            latitude = document.data[Collection.Prestamo.LATITUDE].toString(),
                            longitude = document.data[Collection.Prestamo.LONGITUDE].toString(),
                            montoFinal = document.data[Collection.Prestamo.MONTOFINAL].toString(),
                            employeName = document.data[Collection.Prestamo.EMPLOYE].toString(),
                            isActived = if (document.data[Collection.Prestamo.ACTIVE].toString() != STATUSPRESTAMO[2])
                                getStatus(
                                    document.data[Collection.Prestamo.SALDO].toString().toFloat(),
                                    document.data[Collection.Prestamo.INTERESDIARIO].toString()
                                        .toFloat(),
                                    document.data[Collection.Prestamo.MONTOFINAL].toString()
                                        .toFloat(),
                                    document.data[Collection.Prestamo.FECHAINIT].toString()
                                )
                            else
                                document.data[Collection.Prestamo.ACTIVE].toString(),
                            saldo = document.data[Collection.Prestamo.SALDO].toString(),
                            dateRegisterPay = document.data[Collection.Prestamo.DATEPAY].toString(),
                            noAbonos = document.data[Collection.Prestamo.NOABONOS].toString(),
                            totalAbonos = document.data[Collection.Prestamo.TOTALABONOS].toString(),
                            noPrestamo = document.data[Collection.Prestamo.NOPRESTAMO].toString(),
                        )
                    )
                }
                if (listPrestamo.isEmpty()) {
                    onAction.postValue(DailyAction.OnEmpty)
                } else {
                    var triple = Triple(totalPrestado, totalGanancia, totalInteres)
                    onAction.postValue(DailyAction.GetAllUser(listPrestamo.sorted().toHashSet(), triple))
                    getHistoryPayAll()
                }
            }
        }
    }

    private fun getStatus(
        saldoActual: Float,
        pagoSugerido: Float,
        montoFinal: Float,
        fechaInicio: String,
    ): String {
        var status: String = Collection.Prestamo.STATUSPRESTAMO[2].toString()
        if (saldoActual == montoFinal) {
            status = Collection.Prestamo.STATUSPRESTAMO[1].toString()
        } else if (saldoActual < montoFinal) {
            val dias = DateUtil().getDiasTrans(fechaInicio)
            val shoudbe: Float = dias * pagoSugerido
            if (saldoActual >= shoudbe) {
                status = Collection.Prestamo.STATUSPRESTAMO[0].toString()
            } else {
                status = Collection.Prestamo.STATUSPRESTAMO[2].toString()
            }
        }
        return status
    }

    fun getHistoryPayAll() {
        list = mutableListOf()
        db?.collection(Collection.Prestamo.REGISTROPAGO)?.addSnapshotListener { snapshots, e ->
            if (snapshots != null) {
                for (document in snapshots) {
                    if (fechaBuscar == document.data[Collection.Prestamo.DATEPAY].toString()
                    ) {
                        list.add(
                            document.data[Collection.Prestamo.MONTOFINAL].toString()
                                .toFloat()
                        )
                    }
                }
            }
            onAction.postValue(DailyAction.UpdateTotal(list.sum()))
        }
    }

    fun getHistoryPay() {
        list = mutableListOf()
        db?.collection(Collection.Prestamo.REGISTROPAGO)?.addSnapshotListener { snapshots, e ->
            if (snapshots != null) {
                for (document in snapshots) {
                    if (document.data[Collection.Prestamo.EMPLOYE].toString() == userName &&
                        fechaBuscar == document.data[Collection.Prestamo.DATEPAY].toString()
                    ) {
                        list.add(
                            document.data[Collection.Prestamo.MONTOFINAL].toString()
                                .toFloat()
                        )
                    }
                }
            }
            onAction.postValue(DailyAction.UpdateTotal(list.sum()))
        }
    }

    fun getPrestamobyParent() {
        listPrestamo = mutableListOf()
        var totalPrestado = 0F
        var totalInteres = 0F
        var totalGanancia = 0F
        db = FirebaseFirestore.getInstance()
        db?.collection(Collection.Prestamo.REGISTROCREDITO)?.addSnapshotListener { snapshots, e ->
            if (snapshots != null) {
                for (document in snapshots) {
                    if (document.data[Collection.Prestamo.EMPLOYE].toString() == userName
                    ) {
                        totalPrestado += document.data[Collection.Prestamo.MONTOINICIAL].toString()
                            .toFloat()
                        totalInteres += document.data[Collection.Prestamo.MONTOFINAL].toString()
                            .toFloat()
                        totalGanancia += document.data[Collection.Prestamo.MONTOFINAL].toString()
                            .toFloat().minus(
                                document.data[Collection.Prestamo.MONTOINICIAL].toString()
                                    .toFloat()
                            )
                        listPrestamo.add(
                            Prestamo(
                                fechaInicio = document.data[Collection.Prestamo.FECHAINIT].toString(),
                                fechaFin = document.data[Collection.Prestamo.FECHAEND].toString(),
                                curp = document.data[Collection.Prestamo.CURP].toString(),
                                name = document.data[Collection.Prestamo.USERNAME].toString(),
                                interesDiario = document.data[Collection.Prestamo.INTERESDIARIO].toString(),
                                montoInicial = document.data[Collection.Prestamo.MONTOINICIAL].toString(),
                                latitude = document.data[Collection.Prestamo.LATITUDE].toString(),
                                longitude = document.data[Collection.Prestamo.LONGITUDE].toString(),
                                montoFinal = document.data[Collection.Prestamo.MONTOFINAL].toString(),
                                employeName = document.data[Collection.Prestamo.EMPLOYE].toString(),
                                isActived = document.data[Collection.Prestamo.ACTIVE].toString(),
                                saldo = document.data[Collection.Prestamo.SALDO].toString(),
                                dateRegisterPay = document.data[Collection.Prestamo.DATEPAY].toString(),
                                noAbonos = document.data[Collection.Prestamo.NOABONOS].toString(),
                                totalAbonos = document.data[Collection.Prestamo.TOTALABONOS].toString(),
                                noPrestamo = document.data[Collection.Prestamo.NOPRESTAMO].toString()
                            )
                        )
                    }
                }
                if (listPrestamo.isEmpty()) {
                    onAction.postValue(DailyAction.OnEmpty)
                } else {
                    onAction.postValue(
                        DailyAction.GellUserByEmploye(
                            listPrestamo.toHashSet(),
                            Triple(totalPrestado, totalGanancia, totalInteres)
                        )
                    )
                    getHistoryPay()
                }
            }
        }
    }

    fun getInitPrestamobyParent() {
        listPrestamo = mutableListOf()
        var totalPrestado = 0F
        var totalInteres = 0F
        var totalGanancia = 0F
        db = FirebaseFirestore.getInstance()
        db?.collection(Collection.Prestamo.REGISTROCREDITO)?.addSnapshotListener { snapshots, e ->
            if (snapshots != null) {
                for (document in snapshots) {
                    if (document.data[Collection.Prestamo.EMPLOYE].toString() == userName
                    ) {
                        totalPrestado += document.data[Collection.Prestamo.MONTOINICIAL].toString()
                            .toFloat()
                        totalInteres += document.data[Collection.Prestamo.MONTOFINAL].toString()
                            .toFloat()
                        totalGanancia += document.data[Collection.Prestamo.MONTOFINAL].toString()
                            .toFloat().minus(
                                document.data[Collection.Prestamo.MONTOINICIAL].toString()
                                    .toFloat()
                            )
                        listPrestamo.add(
                            Prestamo(
                                fechaInicio = document.data[Collection.Prestamo.FECHAINIT].toString(),
                                fechaFin = document.data[Collection.Prestamo.FECHAEND].toString(),
                                curp = document.data[Collection.Prestamo.CURP].toString(),
                                interesDiario = document.data[Collection.Prestamo.INTERESDIARIO].toString(),
                                montoInicial = document.data[Collection.Prestamo.MONTOINICIAL].toString(),
                                latitude = document.data[Collection.Prestamo.LATITUDE].toString(),
                                longitude = document.data[Collection.Prestamo.LONGITUDE].toString(),
                                montoFinal = document.data[Collection.Prestamo.MONTOFINAL].toString(),
                                employeName = document.data[Collection.Prestamo.EMPLOYE].toString(),
                                isActived = document.data[Collection.Prestamo.ACTIVE].toString(),
                                saldo = document.data[Collection.Prestamo.SALDO].toString(),
                                dateRegisterPay = document.data[Collection.Prestamo.DATEPAY].toString(),
                                noAbonos = document.data[Collection.Prestamo.NOABONOS].toString(),
                                totalAbonos = document.data[Collection.Prestamo.TOTALABONOS].toString(),
                                noPrestamo = document.data[Collection.Prestamo.NOPRESTAMO].toString(),
                                name = document.data[Collection.Prestamo.USERNAME].toString()
                            )
                        )
                    }
                }
                if (listPrestamo.isEmpty()) {
                    onAction.postValue(DailyAction.OnEmpty)
                } else {
                    onAction.postValue(
                        DailyAction.GellUserByEmploye(
                            listPrestamo.toHashSet(),
                            Triple(totalPrestado, totalGanancia, totalInteres)
                        )
                    )
                    getHistoryPay()
                }
            }
        }
    }


    fun setFecha(fecha: String) {
        fechaBuscar = fecha
    }

    fun setUserName(user: String) {
        userName = user
    }

    fun getAction(): LiveData<DailyAction> = onAction
}