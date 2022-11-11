package com.calendar.calendar.viewModel.action

import com.calendar.calendar.model.Prestamo

sealed class DailyAction {
    class GetAllUser(val list: HashSet<Prestamo>,val triple: Triple<Float, Float, Float>) : DailyAction()
    class UpdateTotal(val total:Float) : DailyAction()
    object OnLoginError : DailyAction()
    object OnEmpty : DailyAction()
    class GellUserByEmploye(
        val lstPrestamo: HashSet<Prestamo>,
        val triple: Triple<Float, Float, Float>
    ) : DailyAction()

    class SetEmployeeList(val list: HashSet<String>) : DailyAction()
}