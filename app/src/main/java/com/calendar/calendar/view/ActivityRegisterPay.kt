package com.calendar.calendar.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.calendar.calendar.R
import com.calendar.calendar.databinding.ActivityRegisterPayBinding
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Employee
import com.calendar.calendar.utils.SessionManager
import com.calendar.calendar.view.adapter.HistoryAdapter
import com.calendar.calendar.viewModel.RegisterPayViewModel
import com.calendar.calendar.viewModel.action.RegisterPayAction
import java.util.*

class ActivityRegisterPay : BaseActivity(), LocationListener {
    private lateinit var binding: ActivityRegisterPayBinding
    private val viewModel: RegisterPayViewModel by viewModels()
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var session: SessionManager? = null
    private var employee: Employee? = null
    private var adapterHistory: HistoryAdapter? = null
    private var lat: String? = ""
    private var lon: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getEmployeePreference()
        binding = ActivityRegisterPayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observerViewModel()
        val curp: String = intent.getStringExtra(Collection.User.USER).toString()
        val noPrestamo: String = intent.getStringExtra(Collection.Prestamo.NOPRESTAMO).toString()
        val fechaFin: String = intent.getStringExtra(Collection.Prestamo.FECHAEND).toString()
        val fechaInit: String = intent.getStringExtra(Collection.Prestamo.FECHAINIT).toString()
        val totalAbonos: String = intent.getStringExtra(Collection.Prestamo.TOTALABONOS).toString()
        val noAbonos: String = intent.getStringExtra(Collection.Prestamo.NOABONOS).toString()
        val pagoSugerido: String =
            intent.getStringExtra(Collection.Prestamo.INTERESDIARIO).toString()
        val saldo: Float = intent.getStringExtra(Collection.Prestamo.SALDO)?.toFloat() ?: 0f
        val montoFinal: Float =
            intent.getStringExtra(Collection.Prestamo.MONTOFINAL)?.toFloat() ?: 0f
        binding.curp.setText(curp)
        viewModel.setCurp(curp)
        viewModel.getHistoryPay(noPrestamo)
        binding.pagoSugerido.setText(pagoSugerido)
        binding.btnSave.setOnClickListener {
            if (binding.monto.text?.isNotEmpty() == true && binding.monto.text.toString()
                    .toFloat() > 0
            ) {
                if (lat != null && lon != null) {
                    viewModel.register(
                        curp,
                        binding.monto.text.toString(),
                        saldo,
                        employee?.userName.toString(),
                        montoFinal,
                        pagoSugerido.toFloat(),
                        fechaFin, totalAbonos.toInt(), noAbonos.toInt(),
                        noPrestamo,
                        fechaInit,
                        lat.toString(),
                        lon.toString(),
                    )
                }

            } else {
                binding.monto.error = getString(R.string.required)
            }
        }
        val layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        adapterHistory = HistoryAdapter(this, employee?.type.toString(), ::locationSelected)
        binding.recyclerhistory.layoutManager = layoutManager
        binding.recyclerhistory.adapter = adapterHistory
        getLocation()
    }

    private fun locationSelected(lat: String, lon: String) {
        if(!lat.isNullOrEmpty() || !lon.isNullOrEmpty()){
            val uri = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", lat.toFloat(), lon.toFloat())
            val intent: Intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }
    }

    private fun observerViewModel() {
        viewModel.getAction().observe(this) { action ->
            when (action) {
                is RegisterPayAction.OnSuccess -> {
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(Intent(this, MenuUser::class.java))
                }
                is RegisterPayAction.OnSuccess1 -> {
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(Intent(this, MenuUser::class.java))
                }
                is RegisterPayAction.ShowInfoPrestamo -> {
                    binding.history.text = binding.history.text.toString().plus(action.history)
                }
                is RegisterPayAction.ShowInfoPrestamoAdapter -> {
                    adapterHistory?.submitList(mutableListOf())
                    adapterHistory?.submitList(action.prestamoList)
                }
                else -> {}
            }
        }
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

    private fun getEmployeePreference() {
        session = SessionManager(applicationContext)
        employee = session?.getEmployee()
    }

    override fun onResume() {
        super.onResume()
        validateTime()
    }

}