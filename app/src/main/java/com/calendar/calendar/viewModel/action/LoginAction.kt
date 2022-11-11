package com.calendar.calendar.viewModel.action

import com.calendar.calendar.model.Employee

sealed class LoginAction {
    class OnLoginSuccess(val employee: Employee) : LoginAction()
    object OnLoginError : LoginAction()
    object Inactived : LoginAction()
}