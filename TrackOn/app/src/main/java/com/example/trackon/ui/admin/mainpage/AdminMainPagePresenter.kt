package com.example.trackon.ui.admin.mainpage

import com.example.trackon.model.AuthModel
import com.example.trackon.model.ReportModel
import com.example.trackon.model.callback.DeleteReportListener
import com.example.trackon.model.callback.LoadAllReportListener
import com.example.trackon.model.callback.LoadTokenListener
import com.example.trackon.model.callback.PostSubmitReportListener
import com.example.trackon.model.data.ReportItem
import com.example.trackon.model.data.Token

class AdminMainPagePresenter(val view: AdminMainPageContract.View) : AdminMainPageContract.Presenter {

    val authModel = AuthModel()
    val reportModel = ReportModel()

    override fun getAllReports(token: String) {
        reportModel.getAllReport(token, object : LoadAllReportListener {
            override fun loadReports(reportList: ArrayList<ReportItem>) {
                view.loadReports(reportList)
            }

            override fun notAdmin() {
                view.notAdmin()
            }

            override fun onFail() {
                view.tokenError()
                view.retryLoadReports()
            }

        })
    }

    override fun retryGetAllReports(token: String) {
        reportModel.getAllReport(token, object : LoadAllReportListener {
            override fun loadReports(reportList: ArrayList<ReportItem>) {
                view.loadReports(reportList)
            }

            override fun notAdmin() {
                view.notAdmin()
            }

            override fun onFail() {
                view.gotoLogin()
            }

        })
    }

    override fun submitReport(token: String, reportId: Long) {
        reportModel.submitReport(token, reportId, object : PostSubmitReportListener {
            override fun submitReport() {
                view.successSubmit()
            }

            override fun notAdmin() {
                view.notAdmin()
            }

            override fun onFail() {
                view.tokenError()
                view.retrySubmit(reportId)
            }

        })
    }

    override fun retrySubmitReport(token: String, reportId: Long) {
        reportModel.deleteReport(token, reportId, object : DeleteReportListener {
            override fun deleteReport() {
                view.successDelete()
            }

            override fun notAdmin() {
                view.notAdmin()
            }

            override fun onFail() {
                view.gotoLogin()
            }

        })
    }

    override fun deleteReport(token: String, reportId: Long) {
        reportModel.deleteReport(token, reportId, object : DeleteReportListener {
            override fun deleteReport() {
                view.successDelete()
            }

            override fun notAdmin() {
                view.notAdmin()
            }

            override fun onFail() {
                view.tokenError()
                view.retryDelete(reportId)
            }

        })

    }

    override fun retryDeleteReport(token: String, reportId: Long) {
        reportModel.deleteReport(token, reportId, object : DeleteReportListener {
            override fun deleteReport() {
                view.successDelete()
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