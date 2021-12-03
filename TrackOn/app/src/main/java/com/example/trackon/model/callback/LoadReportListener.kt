package com.example.trackon.model.callback

import com.example.trackon.model.data.ReportItem

interface LoadReportListener {
    fun loadReports(reportList: ArrayList<ReportItem>)
    fun onFail()
}