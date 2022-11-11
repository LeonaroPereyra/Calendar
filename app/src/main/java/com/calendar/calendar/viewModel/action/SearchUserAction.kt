package com.calendar.calendar.viewModel.action

import com.calendar.calendar.model.User

sealed class SearchUserAction {
    class GetAllUser(val userList: HashSet<User>) : SearchUserAction()
    object GetEmpty : SearchUserAction()
}