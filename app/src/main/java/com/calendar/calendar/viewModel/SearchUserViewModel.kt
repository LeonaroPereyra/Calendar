package com.calendar.calendar.viewModel

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.ONE_MEGABYTE
import com.calendar.calendar.model.User
import com.calendar.calendar.viewModel.action.SearchUserAction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.stream.Collectors

class SearchUserViewModel : ViewModel() {
    private var db: FirebaseFirestore? = null
    private var listUser: MutableList<User> = mutableListOf<User>()
    private var onSearchUserAction: MutableLiveData<SearchUserAction> = MutableLiveData()

    fun getAllUser() {
        db = FirebaseFirestore.getInstance()
        db?.collection(Collection.User.USER)?.addSnapshotListener { snapshots, e ->
            if (snapshots != null) {
                for (document in snapshots) {
                    listUser.add(
                        User(
                            name = document.data[Collection.User.NAME].toString(),
                            lastName = document.data[Collection.User.LASTNAME].toString(),
                            curp = document.data[Collection.User.CURP].toString(),
                            aka = document.data[Collection.User.ALIAS].toString(),
                            employeeName = document.data[Collection.User.EMPLOYE].toString(),
                            active = document.data[Collection.User.ACTIVE].toString(),
                            noPrestamo = document.data[Collection.User.NOPRESTAMO].toString(),
                            image = document.data[Collection.User.IMAGEFONT].toString(),
                            imageBack = document.data[Collection.User.IMAGEBACK].toString(),
                        )
                    )
                }
                if (listUser.isEmpty()) {
                    onSearchUserAction.postValue(SearchUserAction.GetEmpty)
                } else {
                    onSearchUserAction.postValue(SearchUserAction.GetAllUser(listUser.toHashSet()))
                }
            }
        }
    }

    fun filterable(newText: String?, isName: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isName) {
                val newList =
                    listUser.stream().filter { user ->
                        var nameComplete = user.name + " " + user.lastName
                        nameComplete.contains(newText?.toUpperCase() ?: "") == true
                    }
                        .collect(Collectors.toList()).toHashSet()
                if (newList.isEmpty()) {
                    onSearchUserAction.postValue(SearchUserAction.GetEmpty)
                } else {
                    onSearchUserAction.postValue(SearchUserAction.GetAllUser(newList))
                }
            } else {
                val newList =
                    listUser.stream().filter { user ->
                        user.curp?.contains(newText?.toUpperCase() ?: "") == true
                    }
                        .collect(Collectors.toList()).toHashSet()
                if (newList.isEmpty()) {
                    onSearchUserAction.postValue(SearchUserAction.GetEmpty)
                } else {
                    onSearchUserAction.postValue(SearchUserAction.GetAllUser(newList))
                }
            }

        }
    }


    fun getUserAction(): LiveData<SearchUserAction> = onSearchUserAction
}