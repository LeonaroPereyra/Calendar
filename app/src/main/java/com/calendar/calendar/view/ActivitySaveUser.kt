package com.calendar.calendar.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.calendar.calendar.R
import com.calendar.calendar.databinding.ActivitySaveUserBinding
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Collection.User.STATUSUSER
import com.calendar.calendar.model.Employee
import com.calendar.calendar.model.IMAGBACK
import com.calendar.calendar.model.IMAGE
import com.calendar.calendar.model.User
import com.calendar.calendar.utils.SessionManager
import com.calendar.calendar.viewModel.SaveUserViewModel
import com.calendar.calendar.viewModel.action.SaveUserAction
import java.io.File
import java.io.IOException


class ActivitySaveUser : BaseActivity(), LocationListener {

    private lateinit var binding: ActivitySaveUserBinding
    private val viewModel: SaveUserViewModel by viewModels()
    private var currentPhotoPath: String = ""
    private var currentPhotoPathBack: String = ""
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var session: SessionManager? = null
    private var employee: Employee? = null
    private var curp: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getEmployeePreference()
        binding = DataBindingUtil.setContentView<ActivitySaveUserBinding>(
            this,
            R.layout.activity_save_user
        )
        binding.userVM = SaveUserViewModel()
        binding.camara.setOnClickListener()
        {
            abrirCamara(IMAGE)
        }
        binding.camaraBack.setOnClickListener()
        {
            abrirCamara(IMAGBACK)
        }
        binding.saveButton.setOnClickListener {
            if (currentPhotoPath.isEmpty()) {
                binding.camara.error = "Tomar foto"
            } else if (currentPhotoPathBack.isEmpty()) {
                binding.camaraBack.error = "Tomar foto"
            } else {
                if (!validateIsEmpty()) {
                    viewModel.saveUser(createUser())
                    showProgressDialog()
                }
            }
        }
        curp = intent.getStringExtra(Collection.User.USER).toString()
        binding.curp.setText(curp)
        observeViewModel()
        getLocation()
    }


    private fun validateIsEmpty(): Boolean {
        var isEmpty = false
        if (binding.name.text?.isEmpty() == true) {
            isEmpty = true
            binding.name.error = getString(R.string.required)
        } else if (binding.lastName.text?.isEmpty() == true) {
            isEmpty = true
            binding.lastName.error = getString(R.string.required)
        } else if (binding.curp.text?.isEmpty() == true) {
            isEmpty = true
            binding.curp.error = getString(R.string.required)
        } else if (binding.aka.text?.isEmpty() == true
        ) {
            isEmpty = true
            binding.aka.error = getString(com.calendar.calendar.R.string.required)
        } else if (binding.aka.text?.length !=10
        ) {
            isEmpty = true
            binding.aka.error = getString(com.calendar.calendar.R.string.malformat)
        }
        return isEmpty
    }

    private fun createUser(): User {
        return User(
            name = binding.name.text.toString(),
            lastName = binding.lastName.text.toString(),
            curp = binding.curp.text.toString(),
            aka = binding.aka.text.toString(),
            image = currentPhotoPath,
            imageBack = currentPhotoPathBack,
            employeeName = employee?.userName,
            active = STATUSUSER[0],
            noPrestamo = "0"
        )
    }

    private fun observeViewModel() {
        viewModel.getSaveUserAction().observe(this) { action ->
            dismissProgressDialog()
            when {
                action is SaveUserAction.OnSuccess -> {
                    Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show()
                    var intent = Intent(
                        this, ActivitySearchUser::class.java
                    )
                    startActivity(intent)
                    this.finish()
                }
                action is SaveUserAction.OnError -> {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE && resultCode == RESULT_OK) {
            val img: Drawable =
                resources.getDrawable(com.calendar.calendar.R.drawable.camara_accepted)
            binding.camara.setCompoundDrawables(img, null, null, null)
        } else if (requestCode == IMAGBACK && resultCode == RESULT_OK) {
            val img: Drawable =
                resources.getDrawable(com.calendar.calendar.R.drawable.camara_accepted)
            binding.camaraBack.setCompoundDrawables(img, null, null, null)
        } else {
            currentPhotoPath = ""
        }
    }

    @Throws(IOException::class)
    private fun crearImagen(): File? {
        val nombreImagen = "front_"
        val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imagen = File.createTempFile(nombreImagen, ".jpg", directorio)
        currentPhotoPath = imagen.absolutePath
        return imagen
    }

    @Throws(IOException::class)
    private fun crearImagenBack(): File? {
        val nombreImagen = "back_"
        val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imagen = File.createTempFile(nombreImagen, ".jpg", directorio)
        currentPhotoPathBack = imagen.absolutePath
        return imagen
    }

    @Throws(IOException::class)
    private fun abrirCamara(type: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var imagenArchivo: File? = null
        var requestCode: Int = 0
        try {
            when (type) {
                IMAGBACK -> {
                    imagenArchivo = crearImagenBack()
                    requestCode = IMAGBACK
                }
                IMAGE -> {
                    imagenArchivo = crearImagen()
                    requestCode = IMAGE
                }
            }

        } catch (ex: IOException) {
            Log.e("Error", ex.toString())
        }
        if (imagenArchivo != null) {
            val fotoUri: Uri = FileProvider.getUriForFile(
                this,
                "com.calendar.calendar.fileprovider",
                imagenArchivo
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri)
        }

        startActivityForResult(intent, requestCode)
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        location.latitude
        location.longitude
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this, ActivitySearchUser::class.java)
        startActivity(intent)
    }

    private fun getEmployeePreference() {
        session = SessionManager(applicationContext)
        employee = session?.getEmployee()
    }

    override fun onResume() {
        super.onResume()
        validateTime()
    }

}