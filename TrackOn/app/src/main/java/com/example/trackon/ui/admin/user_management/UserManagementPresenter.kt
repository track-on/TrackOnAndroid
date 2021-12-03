package com.example.trackon.ui.admin.user_management

import com.example.trackon.model.AuthModel
import com.example.trackon.model.ReportModel
import com.example.trackon.model.UserModel
import com.example.trackon.model.callback.LoadAllUserListener
import com.example.trackon.model.callback.LoadTokenListener
import com.example.trackon.model.callback.PostMakeAdminListener
import com.example.trackon.model.data.Token
import com.example.trackon.model.data.User

class UserManagementPresenter(val view : UserManagementContract.View) : UserManagementContract.Presenter {

    val authModel = AuthModel()
    val userModel = UserModel()

    override fun getUsers(token: String) {
        userModel.getAllUser(token, object : LoadAllUserListener {
            override fun loadUser(users: ArrayList<User>) {
                view.setUsers(users)
            }

            override fun notAdmin() {
                view.notAdmin()
            }

            override fun onFail() {
                view.tokenError()
                view.retrySetUser()
            }

        })
    }

    override fun retryGetUser(token: String) {
        userModel.getAllUser(token, object : LoadAllUserListener {
            override fun loadUser(users: ArrayList<User>) {
                view.setUsers(users)
            }

            override fun notAdmin() {
                view.notAdmin()
            }

            override fun onFail() {
                view.gotoLogin()
            }

        })
    }

    override fun makeAdmin(token: String, userId: Long) {
        userModel.makeAdmin(token, userId, object : PostMakeAdminListener {
            override fun makeAdmin() {
                view.successMakeAdmin()
            }

            override fun notAdmin() {
                view.notAdmin()
            }

            override fun onFail() {
                view.tokenError()
                view.retryMakeAdmin(userId)
            }

        })
    }

    override fun retryMakeAdmin(token: String, userId: Long) {
        userModel.makeAdmin(token, userId, object : PostMakeAdminListener {
            override fun makeAdmin() {
                view.successMakeAdmin()
            }

            override fun notAdmin() {
                view.notAdmin()
            }

            override fun onFail() {
                view.gotoLogin()
            }

        })
    }

    override fun refreshToken(token: String) {
        authModel.refreshToken(token, object : LoadTokenListener {
            override fun loadToken(token: Token) {
                view.setNewToken(token)
            }

            override fun onFail() {
                view.gotoLogin()
            }

        })
    }
}