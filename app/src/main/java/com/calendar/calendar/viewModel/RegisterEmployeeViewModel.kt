package com.calendar.calendar.viewModel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Employee
import com.calendar.calendar.utils.DateUtil
import com.calendar.calendar.viewModel.action.RegisterCreditAction
import com.calendar.calendar.viewModel.action.RegisterEmployeAction
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class RegisterEmployeeViewModel : ViewModel() {

    private var onRegisterAction: MutableLiveData<RegisterEmployeAction> = MutableLiveData()
    private var db: FirebaseFirestore? = null
    private var documentRef: DocumentReference? = null
    private var mAuth: FirebaseAuth? = null
    private var lstEmployee: MutableList<Employee> = mutableListOf()

    init {
        db = FirebaseFirestore.getInstance()
    }


    fun getAllEmploye() {
        lstEmployee= mutableListOf()
        db = FirebaseFirestore.getInstance()
        db?.collection(Collection.Employe.DOCREF)?.addSnapshotListener { snapshots, e ->
            if (snapshots != null) {
                for (document in snapshots) {
                    lstEmployee.add(
                        Employee(
                            userName = document.data[Collection.Employe.USERNAME].toString(),
                            type = document.data[Collection.Employe.TYPE].toString(),
                            isActived = document.data[Collection.Employe.ACTIVE].toString(),
                        )
                    )
                    onRegisterAction.postValue(RegisterEmployeAction.GetAllEmploye(lstEmployee.toHashSet()))
                }
            }

        }
    }

    fun updateEmployee(employee: Employee, activity: Activity) {
        documentRef =
            db?.collection(Collection.Employe.DOCREF)
                ?.document(employee.userName.toString())
        val employeeOrigin: MutableMap<String, Any> = HashMap()
        employeeOrigin[Collection.Employe.TYPE] = employee.type.toString()
        employeeOrigin[Collection.Employe.ACTIVE] = employee.isActived.toString()
        documentRef?.update(employeeOrigin)?.addOnCompleteListener { task: Task<Void> ->
            if (task.isSuccessful) {
                onRegisterAction.postValue(RegisterEmployeAction.OnSuccess)
            }
        }
    }

    fun registerEmployee(employee: Employee, activity: Activity) {
        mAuth = FirebaseAuth.getInstance()
        mAuth?.createUserWithEmailAndPassword(
            employee.userName.orEmpty(),
            employee.password.orEmpty()
        )?.addOnCompleteListener(activity) { task: Task<AuthResult?> ->
            if (task.isSuccessful) {
                db = FirebaseFirestore.getInstance()
                documentRef = db?.collection(Collection.Employe.DOCREF)
                    ?.document(employee.userName.toString())
                val employeDb: MutableMap<String, Any> = HashMap()
                employeDb[Collection.Employe.USERNAME] = employee.userName.toString()
                employeDb[Collection.Employe.TYPE] = employee.type.toString()
                employeDb[Collection.Employe.ACTIVE] = employee.isActived.toString()
                employeDb[Collection.Employe.LATITUDE] = ""
                employeDb[Collection.Employe.LONGITUDE] = ""
                documentRef?.set(employeDb)?.addOnCompleteListener { task: Task<Void> ->
                    if (task.isSuccessful) {
                        onRegisterAction.postValue(RegisterEmployeAction.OnSuccess)
                    } else {
                        onRegisterAction.postValue(RegisterEmployeAction.OnError)
                    }
                }
            } else {
                onRegisterAction.postValue(RegisterEmployeAction.OnError)
            }
        }
    }

    fun getRegiterCreditAction(): LiveData<RegisterEmployeAction> = onRegisterAction

}