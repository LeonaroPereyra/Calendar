package com.calendar.calendar.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.calendar.calendar.R
import com.calendar.calendar.databinding.ActivityLoginBinding
import com.calendar.calendar.model.Collection
import com.calendar.calendar.utils.SessionManager
import com.calendar.calendar.viewModel.LoginViewModel
import com.calendar.calendar.viewModel.action.LoginAction
import com.google.android.material.snackbar.Snackbar

class ActivityLogin : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var session: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        binding.button.setOnClickListener(this)
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        validateTime()
    }

    private fun observeViewModel() {
        viewModel.getLoginAction().observe(
            this
        ) { action ->
            dismissProgressDialog()
            when (action){
                is LoginAction.OnLoginSuccess -> {
                    savePreference(
                        action.employee.userName ?: "",
                        action.employee.type ?: Collection.Employe.TYPEUSER[1]
                    )
                    startActivity(
                        Intent(
                            this,
                            MenuUser::class.java
                        )
                    )
                }
                is LoginAction.OnLoginError -> {
                    binding.button.apply {
                        isActivated = true
                    }
                    Snackbar.make(
                        findViewById(R.id.mainContainer),
                        R.string.error_login,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                 is LoginAction.Inactived -> {
                    Snackbar.make(
                        findViewById(R.id.mainContainer),
                        R.string.inactived,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

    private fun savePreference(userName: String, type: String) {
        session = SessionManager(applicationContext)
        session?.createLoginSession(userName.orEmpty(), type.orEmpty())
    }

    private fun validateEmpty(): Boolean {
        var existError = false
        if (binding.userName.text?.isEmpty() == true) {
            binding.userName.error = this.getString(R.string.required)
            existError = true
        }
        if (binding.password.text?.isEmpty() == true) {
            binding.password.error = this.getString(R.string.required)
            existError = true
        }
        return existError
    }

    override fun onClick(p0: View?) {
        if (!validateEmpty()) {
            binding.button.apply {
                isActivated = false
            }
            viewModel.setUpLogin(
                binding.userName.text.toString(),
                binding.password.text.toString(),
                this
            )
            showProgressDialog()
        }
    }
}
