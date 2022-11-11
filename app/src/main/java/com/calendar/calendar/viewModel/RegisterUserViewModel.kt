package com.calendar.calendar.viewModel

import android.net.Uri
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Collection.User.STATUSUSER
import com.calendar.calendar.model.Prestamo
import com.calendar.calendar.utils.DateUtil
import com.calendar.calendar.viewModel.action.RegisterCreditAction
import com.calendar.calendar.viewModel.action.SaveUserAction
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File

class RegisterUserViewModel() : ViewModel() {
    private var onRegiterCreditAction: MutableLiveData<RegisterCreditAction> = MutableLiveData()
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var documentRef: DocumentReference? = null
    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null

    init {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage?.reference
    }

    fun registerCredit(prestamo: Prestamo, imageFont: String, imageBack: String) {
        val imageReference =
            storageRef?.child("${prestamo.curp}/${prestamo.curp}_${DateUtil().getCurrentDateFormat()}_Front")
        var file = Uri.fromFile(File(imageFont))
        val uploadTask = imageReference?.putFile(file)

        val imageReferenceBack =
            storageRef?.child("${prestamo.curp}/${prestamo.curp}_${DateUtil().getCurrentDateFormat()}_Back")
        var fileBack = Uri.fromFile(File(imageBack))
        val uploadTaskBack = imageReferenceBack?.putFile(fileBack)

        uploadTask?.addOnFailureListener { exception: Exception? -> }
            ?.addOnSuccessListener { task: UploadTask.TaskSnapshot ->
                uploadTaskBack?.addOnSuccessListener { task1: UploadTask.TaskSnapshot ->

                    documentRef =
                        db?.collection(Collection.Prestamo.REGISTROCREDITO)?.document(prestamo.curp)
                    val creditoDb: MutableMap<String, Any> = HashMap()
                    creditoDb[Collection.Prestamo.CURP] = prestamo.curp
                    creditoDb[Collection.Prestamo.USERNAME] = prestamo.name
                    creditoDb[Collection.Prestamo.FECHAINIT] = prestamo.fechaInicio
                    creditoDb[Collection.Prestamo.FECHAEND] = prestamo.fechaFin
                    creditoDb[Collection.Prestamo.MONTOINICIAL] = prestamo.montoInicial
                    creditoDb[Collection.Prestamo.MONTOFINAL] = prestamo.montoFinal
                    creditoDb[Collection.Prestamo.INTERESDIARIO] = prestamo.interesDiario
                    creditoDb[Collection.Prestamo.LATITUDE] = prestamo.latitude
                    creditoDb[Collection.Prestamo.LONGITUDE] = prestamo.longitude
                    creditoDb[Collection.Prestamo.EMPLOYE] = prestamo.employeName
                    creditoDb[Collection.Prestamo.ACTIVE] = Collection.Prestamo.STATUSPRESTAMO[0]
                    creditoDb[Collection.Prestamo.SALDO] = prestamo.saldo
                    creditoDb[Collection.Prestamo.DATEPAY] = prestamo.dateRegisterPay
                    creditoDb[Collection.Prestamo.TOTALABONOS] = prestamo.totalAbonos
                    creditoDb[Collection.Prestamo.NOABONOS] = prestamo.noAbonos
                    creditoDb[Collection.Prestamo.NOPRESTAMO] = prestamo.noPrestamo

                    documentRef?.set(creditoDb)?.addOnCompleteListener { task: Task<Void> ->
                        if (task.isSuccessful) {
                            documentRef =
                                db?.collection(Collection.User.USER)?.document(prestamo.curp)
                            val userOrigin: MutableMap<String, Any> = HashMap()
                            userOrigin[Collection.User.ACTIVE] = STATUSUSER[1].toString()
                            userOrigin[Collection.User.NOPRESTAMO] = prestamo.noPrestamo
                            userOrigin[Collection.User.EMPLOYE] = prestamo.employeName
                            documentRef?.update(userOrigin)
                                ?.addOnCompleteListener { task: Task<Void> ->
                                    if (task.isSuccessful) {
                                        onRegiterCreditAction.postValue(RegisterCreditAction.OnSuccess)
                                    }
                                }
                        } else {
                            onRegiterCreditAction.postValue(RegisterCreditAction.OnError)
                        }
                    }?.addOnFailureListener {
                        onRegiterCreditAction.postValue(
                            RegisterCreditAction.OnError
                        )
                    }
                }
            }
    }

    fun getRegiterCreditAction(): LiveData<RegisterCreditAction> = onRegiterCreditAction

}