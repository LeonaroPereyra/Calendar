package com.calendar.calendar.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Collection.User.STATUSUSER
import com.calendar.calendar.model.User
import com.calendar.calendar.utils.DateUtil
import com.calendar.calendar.viewModel.action.SaveUserAction
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File

class SaveUserViewModel : ViewModel() {
    private var onSaveUserAction: MutableLiveData<SaveUserAction> =
        MutableLiveData<SaveUserAction>()
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var documentRef: DocumentReference? = null
    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null

    init {
        mAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage?.reference
    }

    fun getSaveUserAction(): LiveData<SaveUserAction> = onSaveUserAction

    fun saveUser(user: User) {
        val imageReference = storageRef?.child("${user.curp}/${user.curp}_${DateUtil().getCurrentDateFormat()}_Front")
        var file = Uri.fromFile(File(user.image))
        val uploadTask = imageReference?.putFile(file)

        val imageReferenceBack = storageRef?.child("${user.curp}/${user.curp}_${DateUtil().getCurrentDateFormat()}_Back")
        var fileBack = Uri.fromFile(File(user.imageBack))
        val uploadTaskBack = imageReferenceBack?.putFile(fileBack)

        uploadTask?.addOnFailureListener { exception: Exception? -> }
            ?.addOnSuccessListener { task: UploadTask.TaskSnapshot ->
                uploadTaskBack?.addOnSuccessListener {task1: UploadTask.TaskSnapshot ->
                    db = FirebaseFirestore.getInstance()
                    documentRef = db?.collection(Collection.User.USER)?.document(user.curp ?: "")
                    val userDB: MutableMap<String, Any> = HashMap()
                    userDB[Collection.User.NAME] = user.name?.toUpperCase().orEmpty()
                    userDB[Collection.User.LASTNAME] = user.lastName?.toUpperCase().orEmpty()
                    userDB[Collection.User.CURP] = user.curp?.toUpperCase().orEmpty()
                    userDB[Collection.User.ALIAS] = user.aka.orEmpty()
                    userDB[Collection.User.IMAGEFONT] = task.metadata?.path.orEmpty()
                    userDB[Collection.User.IMAGEBACK] = task1.metadata?.path.orEmpty()
                    userDB[Collection.User.EMPLOYE] =user.employeeName?.toString().orEmpty()
                    userDB[Collection.User.ACTIVE] = user.active?.toString().orEmpty()
                    userDB[Collection.User.NOPRESTAMO] = user.noPrestamo?.toString().orEmpty()

                    documentRef?.set(userDB)?.addOnCompleteListener { taskUpload: Task<Void?> ->
                        if (taskUpload.isSuccessful) {
                            onSaveUserAction.postValue(SaveUserAction.OnSuccess)
                        } else {
                            onSaveUserAction.postValue(SaveUserAction.OnError)
                        }
                    }
                }

            }

    }

}