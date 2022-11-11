package com.calendar.calendar.view

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.calendar.calendar.R
import com.calendar.calendar.databinding.ActivityRegisterEmployeeBinding
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Employee
import com.calendar.calendar.view.adapter.EmployeAdapter
import com.calendar.calendar.viewModel.RegisterEmployeeViewModel
import com.calendar.calendar.viewModel.action.RegisterEmployeAction
import com.google.android.material.snackbar.Snackbar


class ActivityRegisterEmployee : BaseActivity() {
    private lateinit var binding: ActivityRegisterEmployeeBinding
    private val viewModel: RegisterEmployeeViewModel by viewModels()
    private var adapter: EmployeAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ActivityRegisterEmployeeBinding>(
                this,
                R.layout.activity_register_employee
            )
        observeViewModel()
        setUpView()
    }

    private fun employeSelected(employee: Employee) {
        binding.apply {
            userName.setText(employee.userName?.orEmpty())

        }
    }

    private fun setUpRecycler() {
        val layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        adapter = EmployeAdapter(this, ::employeSelected)
        binding.rvEmploye.layoutManager = layoutManager
        binding.rvEmploye.adapter = adapter
    }

    private fun setUpView() {
        setUpRecycler()
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                Collection.Employe.STATUS
            )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = arrayAdapter

        val arrayAdapterType: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                Collection.Employe.TYPEUSER
            )
        arrayAdapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = arrayAdapterType
        binding.saveButton.setOnClickListener() {

            startActivity(Intent(this, ActivityEmployee::class.java))
            //   viewModel.registerEmployee(createEmploye(), this)
            //   showProgressDialog()

        }
        binding.btnUpdate.setOnClickListener {
            if (!validateIsEmpty()) {
                viewModel.updateEmployee(createEmploye(), this)
                showProgressDialog()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.getRegiterCreditAction().observe(this) { action ->
            dismissProgressDialog()
            when (action) {
                is RegisterEmployeAction.OnSuccess -> {
                    Snackbar.make(
                        findViewById(R.id.mainContainer),
                        R.string.save_employe, Snackbar.LENGTH_SHORT
                    ).show()
                    adapter?.submitList(mutableListOf())
                    viewModel.getAllEmploye()
                }
                is RegisterEmployeAction.OnError -> {
                    finish()
                }
                is RegisterEmployeAction.GetAllEmploye -> {
                    adapter?.submitList(mutableListOf())
                    adapter?.submitList(action.lstEmploye.toList())
                }
            }
        }
    }

    private fun validateIsEmpty(): Boolean {
        var isEmpty = false
        if (binding.userName.text?.isEmpty() == true) {
            isEmpty = true
            binding.userName.error = getString(com.calendar.calendar.R.string.required)
        }
        return isEmpty
    }

    private fun createEmploye(): Employee {
        return Employee(
            userName = binding.userName.text.toString(),
            type = binding.spinnerType.selectedItem.toString(),
            isActived = binding.spinnerStatus.selectedItem.toString()
        )
    }

    override fun onResume() {
        super.onResume()
        validateTime()
        viewModel.getAllEmploye()
    }

}