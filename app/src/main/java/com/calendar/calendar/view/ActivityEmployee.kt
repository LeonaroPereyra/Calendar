package com.calendar.calendar.view

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.navigation.ui.AppBarConfiguration
import com.calendar.calendar.R
import com.calendar.calendar.databinding.ActivityEmployeeBinding
import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Employee
import com.calendar.calendar.viewModel.RegisterEmployeeViewModel
import com.calendar.calendar.viewModel.action.RegisterEmployeAction

class ActivityEmployee : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityEmployeeBinding
    private val viewModel: RegisterEmployeeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
        setUpView()
    }

    private fun setUpView() {
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
            if (!validateIsEmpty()) {
                viewModel.registerEmployee(createEmploye(), this)
                showProgressDialog()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        validateTime()
    }

    private fun validateIsEmpty(): Boolean {
        var isEmpty = false
        if (binding.userName.text?.isEmpty() == true) {
            isEmpty = true
            binding.userName.error = getString(com.calendar.calendar.R.string.required)
        } else if (binding.password.text?.isEmpty() == true && binding.password.text?.length!! > 6) {
            isEmpty = true
            binding.password.error = getString(com.calendar.calendar.R.string.required)
        }
        return isEmpty
    }

    private fun observeViewModel() {
        viewModel.getRegiterCreditAction().observe(this) { action ->
            dismissProgressDialog()
            when (action) {
                is RegisterEmployeAction.OnSuccess -> {
                    finish()
                }
                is RegisterEmployeAction.OnError -> {
                    finish()
                }
                else -> {
                    finish()
                }
            }
        }
    }

    private fun createEmploye(): Employee {
        return Employee(
            userName = binding.userName.text.toString(),
            password = binding.password.text.toString(),
            type = binding.spinnerType.selectedItem.toString(),
            isActived = binding.spinnerStatus.selectedItem.toString()
        )
    }
}
