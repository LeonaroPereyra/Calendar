package com.calendar.calendar.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calendar.calendar.viewModel.action.GalleryAction
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference


class GalleryViewModel : ViewModel() {
    private var onAction: MutableLiveData<GalleryAction> = MutableLiveData()
    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null
    private var imagelist: MutableList<Pair<String,String>> = mutableListOf()

    init {
        storage = FirebaseStorage.getInstance()
        storageRef = storage?.reference
    }

    fun getImage(path:String) {
        storageRef?.child(path)?.listAll()
            ?.addOnSuccessListener(OnSuccessListener<ListResult> { listResult ->
                for (file in listResult.items) {
                    file.downloadUrl.addOnSuccessListener { uri ->
                        imagelist.add(Pair(uri.toString(),file.path.orEmpty()))
                    }.addOnSuccessListener {
                        onAction.postValue(GalleryAction.GetGallery(imagelist))

                    }
                }
            })
    }

    fun delete (path:String){
        storageRef?.child(path)?.delete()?.addOnSuccessListener {
            onAction.postValue(GalleryAction.DeleteFallery)
        }?.addOnFailureListener {
          Log.e("leo","${it}")
        }
    }

    fun getGalleryAction(): LiveData<GalleryAction> = onAction
}