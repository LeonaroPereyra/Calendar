package com.calendar.calendar.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.calendar.calendar.databinding.DailyWorkBinding
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Collection.Prestamo.STATUSPRESTAMO
import com.calendar.calendar.model.Employee
import com.calendar.calendar.model.Prestamo
import com.calendar.calendar.utils.DateUtil
import com.calendar.calendar.utils.SessionManager
import com.calendar.calendar.view.adapter.PrestamoAdapter
import com.calendar.calendar.viewModel.DailyWorkViewModel
import com.calendar.calendar.viewModel.action.DailyAction
import java.util.*

class DailyWork : BaseActivity(), SearchView.OnQueryTextListener {
    private lateinit var binding: DailyWorkBinding
    val viewModel: DailyWorkViewModel by viewModels()
    private var adapterPrestamo: PrestamoAdapter? = null
    private var session: SessionManager? = null
    private var employee: Employee? = null
    private var nameEmployeSelected: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DailyWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observerViewModel()
        getEmployeePreference()
        initView()
        if (employee?.type == "ADMIN") {
            binding.spinnerEmploye.visibility = View.VISIBLE
            viewModel.getEmployee()
        } else {
            binding.spinnerEmploye.visibility = View.GONE
            initSearch()
        }
    }

    private fun initSearch() {
        binding.inputSearch.setQuery(DateUtil().getCurrentDate(), false)
        viewModel.setFecha(DateUtil().getCurrentDate())
        viewModel.setUserName(employee?.userName.toString())
        viewModel.getInitPrestamobyParent()

    }

    private fun initView() {
        binding.inputSearch.setOnQueryTextListener(this)
        val layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        adapterPrestamo =
            PrestamoAdapter(this, ::prestamoSelected, employee?.type.toString(), ::locationSelected)
        binding.recyclerUser.layoutManager = layoutManager
        binding.recyclerUser.adapter = adapterPrestamo

    }

    private fun prestamoSelected(prestamo: Prestamo) {
        if (prestamo.isActived != STATUSPRESTAMO[1]) {
            var intent = Intent(this, ActivityRegisterPay::class.java)
            intent.putExtra(Collection.User.USER, prestamo.curp)
            intent.putExtra(Collection.Prestamo.INTERESDIARIO, prestamo.interesDiario)
            intent.putExtra(Collection.Prestamo.SALDO, prestamo.saldo)
            intent.putExtra(Collection.Prestamo.MONTOFINAL, prestamo.montoFinal)
            intent.putExtra(Collection.Prestamo.NOABONOS, prestamo.noAbonos)
            intent.putExtra(Collection.Prestamo.FECHAEND, prestamo.fechaFin)
            intent.putExtra(Collection.Prestamo.FECHAINIT, prestamo.fechaInicio)
            intent.putExtra(Collection.Prestamo.TOTALABONOS, prestamo.totalAbonos)
            intent.putExtra(Collection.Prestamo.NOPRESTAMO, prestamo.noPrestamo)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Saldado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun locationSelected(lat: String, lon: String) {
        if(!lat.isNullOrEmpty() || !lat.isNullOrEmpty()){
            val uri = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", lat.toFloat(), lon.toFloat())
            val intent: Intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }else {
            Toast.makeText(this,"No se capturo Ubicacion", Toast.LENGTH_SHORT)
        }
    }

    private fun callSelected(number: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse(number)
        startActivity(callIntent)
    }

    private fun observerViewModel() {
        viewModel.getAction().observe(this) { action ->
            when (action) {
                is DailyAction.GetAllUser -> {
                    setUpViewList()
                    adapterPrestamo?.submitList(mutableListOf())
                    adapterPrestamo?.submitList(action.list.toList().sorted())
                    binding.totalPrestado.setText(action.triple.first.toString())
                    binding.totalGanancia.setText(action.triple.second.toString())
                    binding.totalInteres.setText(action.triple.third.toString())
                }
                is DailyAction.GellUserByEmploye -> {
                    setUpViewList()
                    adapterPrestamo?.submitList(mutableListOf())
                    action.lstPrestamo.toList().sorted()
                    adapterPrestamo?.submitList(action.lstPrestamo.toList())
                    binding.totalPrestado.setText(action.triple.first.toString())
                    binding.totalGanancia.setText(action.triple.second.toString())
                    binding.totalInteres.setText(action.triple.third.toString())
                }
                is DailyAction.OnLoginError -> {
                    binding.recyclerUser.visibility = View.GONE
                    binding.empty.visibility = View.VISIBLE
                }
                is DailyAction.SetEmployeeList -> {
                    setUpSpinnerEmploye(action.list.toMutableList())
                    binding.spinnerEmploye.onItemSelectedListener =
                        object : OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                val item = parent.getItemAtPosition(position)
                                if (position == 0) {
                                    nameEmployeSelected = ""
                                    viewModel.setFecha(binding.inputSearch.query.toString())
                                    viewModel.getAllPrestamo()
                                } else {
                                    nameEmployeSelected = item.toString()
                                    viewModel.setFecha(binding.inputSearch.query.toString())
                                    viewModel.setUserName(item.toString())
                                    viewModel.getPrestamobyParent()
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    initSearch()
                }
                is DailyAction.OnEmpty -> {
                    binding.totalPrestado.setText("0")
                    binding.totalGanancia.setText("0")
                    binding.totalInteres.setText("0")
                    binding.totalPagado.setText("0")
                    setUpViewEmpty()
                }
                is DailyAction.UpdateTotal -> {
                    binding.totalPagado.setText(action.total.toString())
                }
                else -> {}
            }
        }
    }

    private fun setUpViewEmpty() {
        binding.recyclerUser.visibility = View.GONE
        binding.empty.visibility = View.VISIBLE
    }

    private fun setUpViewList() {
        binding.recyclerUser.visibility = View.VISIBLE
        binding.empty.visibility = View.GONE
    }

    private fun setUpSpinnerEmploye(lstEmploye: MutableList<String>) {
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                lstEmploye
            )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEmploye.adapter = arrayAdapter
    }

    private fun getEmployeePreference() {
        session = SessionManager(applicationContext)
        employee = session?.getEmployee()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (employee?.type == "ADMIN") {
            viewModel.setFecha(query.toString())
            if (nameEmployeSelected?.isEmpty() == true) {
                viewModel.setFecha(query.toString())
                viewModel.getAllPrestamo()
            } else {
                viewModel.setFecha(query.toString())
                viewModel.setUserName(nameEmployeSelected.toString())
                viewModel.getPrestamobyParent()
            }
        } else {
            viewModel.setUserName(employee?.userName.toString())
            viewModel.setFecha(query.toString())
            viewModel.getPrestamobyParent()
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()
        validateTime()
    }
}