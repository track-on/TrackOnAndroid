package com.example.trackon.ui.admin.login

import com.example.trackon.model.AuthModel
import com.example.trackon.model.callback.LoadTokenListener
import com.example.trackon.model.data.Token

class AdminLoginPresenter(val view: AdminLoginContract.View) : AdminLoginContract.Presenter {

    val authModel = AuthModel()

    override fun login(id: String, password: String) {
        authModel.adminLogin(id, password, object : LoadTokenListener {
            override fun loadToken(token: Token) {
                view.setToken(token)
            }

            override fun onFail() {
                view.loginFail()
            }

        })
    }
}