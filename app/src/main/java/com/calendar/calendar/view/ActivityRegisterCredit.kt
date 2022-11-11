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
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.calendar.calendar.R
import com.calendar.calendar.databinding.ActivityRegisterBinding
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Collection.User.STATUSUSER
import com.calendar.calendar.model.Employee
import com.calendar.calendar.model.IMAGBACK
import com.calendar.calendar.model.IMAGE
import com.calendar.calendar.model.Prestamo
import com.calendar.calendar.utils.DateUtil
import com.calendar.calendar.utils.SessionManager
import com.calendar.calendar.viewModel.RegisterUserViewModel
import com.calendar.calendar.viewModel.action.RegisterCreditAction
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.util.*


class ActivityRegisterCredit : BaseActivity(), LocationListener {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterUserViewModel by viewModels()
    private var dateEnd: String = ""
    private var curp: String = ""
    private var userName: String = ""
    private var active: String = ""
    private var lat: String? = ""
    private var lon: String? = ""
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var session: SessionManager? = null
    private var employee: Employee? = null
    private var time: String? = ""
    private var currentPhotoPath: String = ""
    private var currentPhotoPathBack: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityRegisterBinding>(
            this,
            R.layout.activity_register
        )
        getEmployeePreference()
        curp = intent.getStringExtra(Collection.User.USER).toString()
        userName = intent.getStringExtra(Collection.Prestamo.USERNAMECOMPLETE).toString()
        active = intent.getStringExtra(Collection.User.ACTIVE).toString()
        binding.curp.setText(curp)
        binding.userName.setText(userName)
        observerViewModel()
        getLocation()
        binding.camara.setOnClickListener()
        {
            abrirCamara(IMAGE)
        }
        binding.camaraBack.setOnClickListener()
        {
            abrirCamara(IMAGBACK)
        }
        binding.saveButton.setOnClickListener {
            if (!validateEmpty() && isValidateData()) {
                if (currentPhotoPath.isEmpty()) {
                    binding.camara.error = "Tomar foto"
                } else if (currentPhotoPathBack.isEmpty()) {
                    binding.camaraBack.error = "Tomar foto"
                } else if (active == STATUSUSER[0].toString()) {
                    createCredit()?.let { it1 -> viewModel.registerCredit(it1,currentPhotoPath,currentPhotoPathBack) }
                }
            }
        }
        binding.btnCalcular.setOnClickListener {
            if (!validateEmpty1() && isValidateData()) {
                setDiasToCalendar()

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDiasToCalendar() {
        time = DateUtil().sumDias(binding.diasPrestamo.text.toString().toLong())
        binding.fechaFin.setText(time.toString())
        binding.pagoDiario.text = calculatedPay()
    }

    private fun isValidateData(): Boolean {
        var status = true
        if (binding.montoFinal.text.toString().toFloat() <= binding.montoInicial.text.toString()
                .toFloat()
        ) {
            status = false
            Toast.makeText(this, "Validar Monto", Toast.LENGTH_SHORT).show()
        }
        return status
    }

    private fun calculatedPay(): String {
        val day = DateUtil().getDias(getDateCalendar())
        return if (day > 0) {
            val pay: Float = binding.montoFinal.text.toString().toFloat() / day
            pay.toString()
        } else {
            ""
        }
    }

    private fun getDateCalendar(): String {
        return time.toString()
    }

    private fun createCredit(): Prestamo? {
        if (lat != null && lon != null) {
            return Prestamo(
                curp = curp,
                name = binding.userName.text.toString(),
                fechaInicio = DateUtil().getCurrentDate(),
                fechaFin = binding.fechaFin.text.toString(),
                interesDiario = binding.pagoDiario.text.toString(),
                montoInicial = binding.montoInicial.text.toString(),
                montoFinal = binding.montoFinal.text.toString(),
                latitude = lat.toString(),
                longitude = lon.toString(),
                employeName = employee?.userName.toString(),
                isActived = Collection.Prestamo.STATUSPRESTAMO[0],
                saldo = "0",
                dateRegisterPay = "",
                totalAbonos = DateUtil().getDias(getDateCalendar()).toString(),
                noAbonos = "0",
                noPrestamo = DateUtil().md5(DateUtil().getTimeStamp())
            )
        } else {
            Snackbar.make(
                findViewById(R.id.mainContainer),
                R.string.not_gps, Snackbar.LENGTH_SHORT
            ).show()
            return null
        }
    }


    private fun validateEmpty1(): Boolean {
        var empty: Boolean = false
        if (binding.montoFinal.text?.isEmpty() == true) {
            binding.montoFinal.error = getString(R.string.required)
            empty = true
        } else if (binding.diasPrestamo.text?.isEmpty() == true) {
            binding.diasPrestamo.error = getString(R.string.required)
            empty = true
        } else if (binding.montoInicial.text?.isEmpty() == true) {
            binding.montoInicial.error = getString(R.string.required)
            empty = true
        }
        return empty
    }

    private fun validateEmpty(): Boolean {
        var empty: Boolean = false
        if (binding.curp.text?.isEmpty() == true) {
            binding.curp.error = getString(R.string.required)
            empty = true
        } else if (binding.montoFinal.text?.isEmpty() == true) {
            binding.montoFinal.error = getString(R.string.required)
            empty = true
        } else if (binding.diasPrestamo.text?.isEmpty() == true) {
            binding.diasPrestamo.error = getString(R.string.required)
            empty = true
        } else if (binding.fechaFin.text?.isEmpty() == true) {
            binding.fechaFin.error = getString(R.string.required)
            empty = true
        }
        return empty
    }


    private fun observerViewModel() {
        viewModel.getRegiterCreditAction().observe(this) { action ->
            when (action) {
                is RegisterCreditAction.OnSuccess -> {
                    Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is RegisterCreditAction.OnError -> {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
        lat = location.latitude.toString()
        lon = location.longitude.toString()
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

    private fun getEmployeePreference() {
        session = SessionManager(applicationContext)
        employee = session?.getEmployee()
    }

    override fun onResume() {
        super.onResume()
        validateTime()
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

}