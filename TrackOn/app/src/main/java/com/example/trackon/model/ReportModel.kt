package com.example.trackon.model

import com.example.trackon.model.callback.*
import com.example.trackon.model.data.ReportItem
import com.example.trackon.model.service.ReportService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReportModel {

    private val reportService: ReportService

    init {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://220.90.237.33:7799")
            .build()
        reportService = retrofit.create(ReportService::class.java)
    }

    fun getMyReport(accessToken: String, loadReportListener: LoadReportListener) {
        val call = reportService.getMyReport(accessToken)
        call.enqueue(object : Callback<ArrayList<ReportItem>> {
            override fun onResponse(call: Call<ArrayList<ReportItem>>, response: Response<ArrayList<ReportItem>>) {
                if(response.code() == 200) response.body()?.let { loadReportListener.loadReports(it) }
                if(response.code() == 403) loadReportListener.onFail()
            }

            override fun onFailure(call: Call<ArrayList<ReportItem>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getAllReport(accessToken: String, loadReportListener: LoadAllReportListener) {
        val call = reportService.getAllReport(accessToken)
        call.enqueue(object : Callback<ArrayList<ReportItem>> {
            override fun onResponse(call: Call<ArrayList<ReportItem>>, response: Response<ArrayList<ReportItem>>) {
                if(response.code() == 200) response.body()?.let { loadReportListener.loadReports(it) }
                if(response.code() == 409) loadReportListener.notAdmin()
                if(response.code() == 403) loadReportListener.onFail()
            }

            override fun onFailure(call: Call<ArrayList<ReportItem>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    fun report(accessToken: String, reportId: Long, message: String, postReportListener: PostReportListener) {
        val call = reportService.report(accessToken, reportId, message)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.code() == 200) postReportListener.postListener()
                if(response.code() == 403) postReportListener.onFail()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun submitReport(accessToken: String, reportId: Long, postSubmitReportListener: PostSubmitReportListener) {
        val call = reportService.submitReport(accessToken, reportId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.code() == 200) postSubmitReportListener.submitReport()
                if(response.code() == 403) postSubmitReportListener.onFail()
                if(response.code() == 409) postSubmitReportListener.notAdmin()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun deleteReport(accessToken: String, reportId: Long, deleteReportListener: DeleteReportListener) {
        val call = reportService.deleteReport(accessToken, reportId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.code() == 200) deleteReportListener.deleteReport()
                if(response.code() == 403) deleteReportListener.onFail()
                if(response.code() == 409) deleteReportListener.notAdmin()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}