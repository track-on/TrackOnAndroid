package com.example.trackon.ui.mainpage

import com.example.trackon.model.data.Token
import com.example.trackon.model.data.User

class MainPageContract {
    interface View {
        fun setName(user: User)
        fun setNewToken(token: Token)
        fun retryName()
        fun tokenError()
        fun gotoLogin()
        fun successReport()
        fun retryReport(token: String, markerId: Long, message: String)
    }

    interface Presenter {
        fun getName(accessToken: String)
        fun retryGetName(accessToken: String)
        fun refreshToken(refreshToken: String)
        fun report(markerId: Long, token: String, message: String)
        fun retryReport(token: String, markerId: Long, message: String)
    }
}