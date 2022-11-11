package com.calendar.calendar.viewModel.action

sealed class RegisterCreditAction {
    object OnSuccess : RegisterCreditAction()
    object OnError : RegisterCreditAction()
}