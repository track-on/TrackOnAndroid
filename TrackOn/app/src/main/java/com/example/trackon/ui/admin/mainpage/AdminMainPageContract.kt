package com.example.trackon.ui.admin.mainpage

import com.example.trackon.model.data.ReportItem
import com.example.trackon.model.data.Token

class AdminMainPageContract {
    interface View {
        fun loadReports(reportItem: ArrayList<ReportItem>)
        fun retryLoadReports()
        fun tokenError()
        fun gotoLogin()
        fun notAdmin()
        fun setNewToken(token: Token)
        fun successSubmit()
        fun retrySubmit(reportId: Long)
        fun successDelete()
        fun retryDelete(reportId: Long)
    }

    interface Presenter {
        fun getAllReports(token: String)
        fun retryGetAllReports(token: String)
        fun submitReport(token: String, reportId: Long)
        fun retrySubmitReport(token: String, reportId: Long)
        fun deleteReport(token: String, reportId: Long)
        fun retryDeleteReport(token: String, reportId: Long)
        fun refreshToken(token: String)
    }
}