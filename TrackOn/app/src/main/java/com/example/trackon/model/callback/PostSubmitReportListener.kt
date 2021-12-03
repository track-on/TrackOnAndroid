package com.example.trackon.model.callback

interface PostSubmitReportListener {
    fun submitReport()
    fun notAdmin()
    fun onFail()
}