package com.calendar.calendar.viewModel.action

sealed class SaveUserAction {
    object OnSuccess : SaveUserAction()
    object OnError : SaveUserAction()
}