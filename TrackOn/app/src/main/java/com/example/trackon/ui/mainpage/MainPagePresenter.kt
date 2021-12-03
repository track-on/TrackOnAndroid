package com.example.trackon.ui.mainpage

import com.example.trackon.model.AuthModel
import com.example.trackon.model.ReportModel
import com.example.trackon.model.UserModel
import com.example.trackon.model.callback.LoadTokenListener
import com.example.trackon.model.callback.LoadUserListener
import com.example.trackon.model.callback.PostReportListener
import com.example.trackon.model.callback.PostSubmitReportListener
import com.example.trackon.model.data.Token
import com.example.trackon.model.data.User

class MainPagePresenter(val view: MainPageContract.View): MainPageContract.Presenter {

    private val signUpModel = UserModel()
    private val authModel = AuthModel()
    private val reportModel = ReportModel()

    private var mainPage:MainPageContract.View = view

    override fun getName(accessToken: String) {
        signUpModel.getUserInfo(accessToken, object : LoadUserListener{
            override fun loadUser(user: User) {
                mainPage.setName(user)
            }

            override fun tokenError() {
                mainPage.tokenError()
            }

        })
    }

    override fun retryGetName(accessToken: String) {
        signUpModel.getUserInfo(accessToken, object : LoadUserListener {
            override fun loadUser(user: User) {
                view.setName(user)
            }

            override fun tokenError() {
                view.gotoLogin()
            }

        })
    }

    override fun refreshToken(refreshToken: String) {
        authModel.refreshToken(refreshToken, object : LoadTokenListener{
            override fun loadToken(token: Token) {
                view.setNewToken(token)
            }

            override fun onFail() {
                mainPage.gotoLogin()
            }

        })
    }

    override fun report(markerId: Long, token: String, message: String) {
        reportModel.report(token, markerId, message, object : PostReportListener{
            override fun postListener() {
                view.successReport()
            }

            override fun onFail() {
                view.tokenError()
            }

        })
    }

    override fun retryReport(token: String, markerId: Long, message: String) {
        reportModel.report(token, markerId, message, object : PostReportListener {
            override fun postListener() {
                view.successReport()
            }

            override fun onFail() {
                view.gotoLogin()
            }

        })
    }
}