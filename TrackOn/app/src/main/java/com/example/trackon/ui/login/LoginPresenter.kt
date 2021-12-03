package com.example.trackon.ui.login

import com.example.trackon.model.AuthModel
import com.example.trackon.model.callback.LoadTokenListener
import com.example.trackon.model.data.Token

class LoginPresenter(view: LoginContract.View): LoginContract.Presenter {

    private val authModel: AuthModel = AuthModel()

    private val loadTokenListener = object: LoadTokenListener {
        override fun loadToken(token: Token) {
            view.setToken(token)
        }

        override fun onFail() {
            view.loginFail()
        }

    }

    override fun login(id: String, password: String) {
        authModel.signIn(id, password, loadTokenListener)
    }
}