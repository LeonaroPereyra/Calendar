package com.calendar.calendar.viewModel.action

import com.calendar.calendar.model.Prestamo

sealed class RegisterPayAction {
    class OnSuccess(val status: String, val curp: String) : RegisterPayAction()
    object OnSuccess1: RegisterPayAction()
    class ShowInfoPrestamo(val history: String) : RegisterPayAction()
    class ShowInfoPrestamoAdapter(val prestamoList: MutableList<Prestamo>) : RegisterPayAction()
}