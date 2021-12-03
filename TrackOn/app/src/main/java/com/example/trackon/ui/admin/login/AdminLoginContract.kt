package com.example.trackon.ui.admin.login

import com.example.trackon.model.data.Token

class AdminLoginContract {
    interface View {
        fun loginFail()
        fun setToken(token: Token)
    }

    interface Presenter {
        fun login(id: String, password: String)
    }
}