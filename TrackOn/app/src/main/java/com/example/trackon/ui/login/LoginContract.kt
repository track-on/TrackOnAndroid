package com.example.trackon.ui.login

import com.example.trackon.model.data.Token

class LoginContract {
    interface View {
        fun loginFail()
        fun setToken(token: Token)
    }

    interface Presenter {
        fun login(id: String, password: String)
    }
}