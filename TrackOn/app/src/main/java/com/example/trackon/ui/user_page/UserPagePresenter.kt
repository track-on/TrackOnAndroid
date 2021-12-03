package com.example.trackon.ui.user_page

import com.example.trackon.model.AuthModel
import com.example.trackon.model.ReportModel
import com.example.trackon.model.UserModel
import com.example.trackon.model.callback.LoadReportListener
import com.example.trackon.model.callback.LoadTokenListener
import com.example.trackon.model.callback.LoadUserListener
import com.example.trackon.model.data.ReportItem
import com.example.trackon.model.data.Token
import com.example.trackon.model.data.User

class UserPagePresenter(val view: UserPageContract.View) : UserPageContract.Presenter {

    private val userModel =  UserModel()
    private val reportModel = ReportModel()
    private val authModel = AuthModel()

    override fun getMyReports(token: String) {
        reportModel.getMyReport(token, object : LoadReportListener {
            override fun loadReports(reportList: ArrayList<ReportItem>) {
                view.loadReports(reportList)
            }

            override fun onFail() {
                view.tokenError()
                view.retryLoadReports()
            }

        })
    }

    override fun retryGetMyReports(token: String) {
        reportModel.getMyReport(token, object : LoadReportListener {
            override fun loadReports(reportList: ArrayList<ReportItem>) {
                view.loadReports(reportList)
            }

            override fun onFail() {
                view.gotoLogin()
            }

        })
    }

    override fun getUser(token: String) {
        userModel.getUserInfo(token, object : LoadUserListener {
            override fun loadUser(user: User) {
                view.setUser(user)
            }

            override fun tokenError() {
                view.tokenError()
                view.retrySetUser()
            }

        })
    }

    override fun retryGetUser(token: String) {
        userModel.getUserInfo(token, object : LoadUserListener {
            override fun loadUser(user: User) {
                view.setUser(user)
            }

            override fun tokenError() {
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