package com.example.trackon.model.callback

interface DeleteReportListener {
    fun deleteReport()
    fun notAdmin()
    fun onFail()
}