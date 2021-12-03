package com.example.trackon.ui.admin.user_management

import com.example.trackon.model.data.Token
import com.example.trackon.model.data.User

class UserManagementContract {
    interface View {
        fun setNewToken(token: Token)
        fun setUsers(users: ArrayList<User>)
        fun successMakeAdmin()
        fun retryMakeAdmin(userId: Long)
        fun tokenError()
        fun notAdmin()
        fun gotoLogin()
        fun retrySetUser()
    }

    interface Presenter {
        fun getUsers(token: String)
        fun retryGetUser(token: String)
        fun makeAdmin(token: String, userId: Long)
        fun retryMakeAdmin(token: String, userId: Long)
        fun refreshToken(token: String)
    }
}