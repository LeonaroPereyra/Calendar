package com.calendar.calendar.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Employee
import com.calendar.calendar.viewModel.action.LoginAction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class LoginViewModel() : ViewModel() {
    private var onLoginAction: MutableLiveData<LoginAction> = MutableLiveData<LoginAction>()
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var documentRef: DocumentReference? = null
    var employee: Employee? = null

    init {
        db = FirebaseFirestore.getInstance()
    }


    fun getLoginAction(): LiveData<LoginAction> = onLoginAction

    fun setUpLogin(userName: String, password: String, activity: Activity) {
        mAuth = FirebaseAuth.getInstance()
        mAuth?.signInWithEmailAndPassword(userName, password)
            ?.addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    var employee = getInfoEmploye(userName)
                    if (employee != null) {
                        if (employee.isActived == Collection.Employe.STATUS[0]){
                            onLoginAction.postValue(LoginAction.OnLoginSuccess(employee))
                        }else {
                            onLoginAction.postValue(LoginAction.Inactived)
                        }
                    } else {
                        onLoginAction.postValue(LoginAction.OnLoginError)
                    }

                }else {
                    onLoginAction.postValue(LoginAction.OnLoginError)
                }
            }?.addOnFailureListener {
                onLoginAction.postValue(LoginAction.OnLoginError)
            }
    }

    private fun getInfoEmploye(userName: String): Employee? {
        documentRef = db?.collection(Collection.Employe.DOCREF)?.document(userName)
        documentRef?.get()?.addOnCompleteListener { task1 ->
            if (task1.isSuccessful) {
                val document: DocumentSnapshot = task1.result as DocumentSnapshot
                if (document.exists()) {
                    employee =Employee(
                        userName = document.data?.get(Collection.Employe.USERNAME).toString(),
                        type = document.data?.get(Collection.Employe.TYPE).toString(),
                        isActived = document.data?.get(Collection.Employe.ACTIVE).toString(),
                        password = ""
                        )
                }
            }
        }?.addOnFailureListener {

        }
        return employee
    }
}