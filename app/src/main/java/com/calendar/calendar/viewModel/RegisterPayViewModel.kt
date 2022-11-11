package com.calendar.calendar.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Collection.Prestamo.STATUSPRESTAMO
import com.calendar.calendar.model.Collection.User.STATUSUSER
import com.calendar.calendar.model.Prestamo
import com.calendar.calendar.utils.DateUtil
import com.calendar.calendar.viewModel.action.RegisterCreditAction
import com.calendar.calendar.viewModel.action.RegisterPayAction
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPayViewModel : ViewModel() {
    private var onRegisterPay: MutableLiveData<RegisterPayAction> = MutableLiveData()
    private var db: FirebaseFirestore? = null
    private var documentRef: DocumentReference? = null
    private var curpUser: String? = ""
    private var lista: MutableList<String> = mutableListOf<String>()
    private var listPrestamo: MutableList<Prestamo> = mutableListOf<Prestamo>()

    init {
        db = FirebaseFirestore.getInstance()
    }

    fun getAction(): LiveData<RegisterPayAction> = onRegisterPay

    fun register(
        curp: String,
        monto: String,
        saldo: Float,
        employeName: String,
        montoFinal: Float,
        pagoSugerido: Float,
        fechaFin: String,
        totalAbonos: Int,
        noAbonos: Int,
        noPrestamo: String,
        fechaInicio: String,
        lat: String,
        lon: String
    ) {
        documentRef =
            db?.collection(Collection.Prestamo.REGISTROPAGO)
                ?.document("${noPrestamo}_${DateUtil().getTimeStamp()}")
        val prestamodb: MutableMap<String, Any> = HashMap()
        prestamodb[Collection.Prestamo.CURP] = curp
        prestamodb[Collection.Prestamo.MONTOFINAL] = monto
        prestamodb[Collection.Prestamo.DATEPAY] = DateUtil().getCurrentDate()
        prestamodb[Collection.Prestamo.EMPLOYE] = employeName
        prestamodb[Collection.Prestamo.NOPRESTAMO] = noPrestamo
        prestamodb[Collection.Prestamo.LATITUDE] = lat
        prestamodb[Collection.Prestamo.LONGITUDE] = lon
        documentRef?.set(prestamodb)?.addOnCompleteListener { task: Task<Void> ->
            if (task.isSuccessful) {
                documentRef =
                    db?.collection(Collection.Prestamo.REGISTROCREDITO)?.document(curp)
                val total = saldo + monto.toFloat()
                val status = getStatus(
                    total,
                    monto.toFloat(),
                    pagoSugerido,
                    montoFinal,
                    fechaFin,
                    totalAbonos,
                    noAbonos, fechaInicio
                )
                val prestamoOrigin: MutableMap<String, Any> = HashMap()
                prestamoOrigin[Collection.Prestamo.SALDO] = total.toString()
                prestamoOrigin[Collection.Prestamo.DATEPAY] = DateUtil().getCurrentDate()
                prestamoOrigin[Collection.Prestamo.NOABONOS] = noAbonos + 1
                prestamoOrigin[Collection.Prestamo.ACTIVE] = status
                prestamoOrigin[Collection.Prestamo.EMPLOYE] = employeName
                prestamoOrigin[Collection.Prestamo.NOPRESTAMO] =
                    if (status == STATUSPRESTAMO[1]) "" else noPrestamo
                documentRef?.update(prestamoOrigin)?.addOnCompleteListener { task: Task<Void> ->
                    if (task.isSuccessful) {
                        documentRef =
                            db?.collection(Collection.User.USER)?.document(curp)
                        val userOrigin: MutableMap<String, Any> = HashMap()
                        userOrigin[Collection.User.ACTIVE] =
                            if (status == STATUSPRESTAMO[1]) STATUSUSER[0] else STATUSUSER[1]
                        userOrigin[Collection.User.NOPRESTAMO] =
                            if (status == STATUSPRESTAMO[1]) "" else noPrestamo
                        documentRef?.update(userOrigin)?.addOnCompleteListener { task: Task<Void> ->
                            if (task.isSuccessful) {
                                onRegisterPay.postValue(RegisterPayAction.OnSuccess(status, curp))
                            }
                        }
                    }
                }
            }
        }
    }

    fun getHistoryPay(noPrestamo: String) {
        db?.collection(Collection.Prestamo.REGISTROPAGO)?.addSnapshotListener { snapshots, e ->
            if (snapshots != null) {
                for (document in snapshots) {
                    if (document.data[Collection.Prestamo.CURP].toString() == (curpUser) &&
                        document.data[Collection.Prestamo.NOPRESTAMO].toString() == (noPrestamo)
                    ) {
                        listPrestamo.add(
                            Prestamo(
                                montoFinal = document.data[Collection.Prestamo.MONTOFINAL].toString(),
                                dateRegisterPay = document.data[Collection.Prestamo.DATEPAY].toString(),
                                latitude = document.data[Collection.Prestamo.LATITUDE].toString(),
                                longitude = document.data[Collection.Prestamo.LONGITUDE].toString(),
                                fechaInicio = document.data[Collection.Prestamo.FECHAINIT].toString(),
                                fechaFin = document.data[Collection.Prestamo.FECHAEND].toString(),
                                curp = document.data[Collection.Prestamo.CURP].toString(),
                                name = document.data[Collection.Prestamo.USERNAME].toString(),
                                interesDiario = document.data[Collection.Prestamo.INTERESDIARIO].toString(),
                                montoInicial = document.data[Collection.Prestamo.MONTOINICIAL].toString(),
                                employeName = document.data[Collection.Prestamo.EMPLOYE].toString(),
                                isActived = document.data[Collection.Prestamo.ACTIVE].toString(),
                                saldo = document.data[Collection.Prestamo.SALDO].toString(),
                                noAbonos = document.data[Collection.Prestamo.NOABONOS].toString(),
                                totalAbonos = document.data[Collection.Prestamo.TOTALABONOS].toString(),
                                noPrestamo =  document.data[Collection.Prestamo.NOPRESTAMO].toString()
                                )
                        )
                        lista.add("\n Fecha Pago ${document.data[Collection.Prestamo.DATEPAY].toString()} Monto$:${document.data[Collection.Prestamo.MONTOFINAL].toString()}")
                    }
                }
                onRegisterPay.postValue(RegisterPayAction.ShowInfoPrestamo(lista.toString()))
                onRegisterPay.postValue(RegisterPayAction.ShowInfoPrestamoAdapter(listPrestamo))
            }
        }
    }

    fun setCurp(curp: String) {
        curpUser = curp
    }

    private fun getStatus(
        saldoActual: Float,
        monto: Float,
        pagoSugerido: Float,
        montoFinal: Float,
        fechFin: String,
        totalAbonos: Int,
        noAbonos: Int,
        fechaInicio: String,
    ): String {
        var status: String = STATUSPRESTAMO[2].toString()
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

}