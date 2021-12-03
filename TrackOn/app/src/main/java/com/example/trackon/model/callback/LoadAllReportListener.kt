package com.example.trackon.model.callback

import com.example.trackon.model.data.ReportItem

interface LoadAllReportListener {
    fun loadReports(reportList: ArrayList<ReportItem>)
    fun notAdmin()
    fun onFail()
}