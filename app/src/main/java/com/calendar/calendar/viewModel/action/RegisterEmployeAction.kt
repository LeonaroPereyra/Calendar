package com.calendar.calendar.viewModel.action

import com.calendar.calendar.model.Collection
import com.calendar.calendar.model.Employee
import com.calendar.calendar.model.User

sealed class RegisterEmployeAction {
    object OnSuccess : RegisterEmployeAction()
    object OnError : RegisterEmployeAction()
    class GetAllEmploye(val lstEmploye: HashSet<Employee>) : RegisterEmployeAction()
}