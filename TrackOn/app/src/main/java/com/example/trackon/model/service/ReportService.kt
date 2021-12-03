package com.example.trackon.model.service

import com.example.trackon.model.data.ReportItem
import retrofit2.Call
import retrofit2.http.*

interface ReportService {
    @GET("/report/me")
    fun getMyReport(@Header("Authorization") accessToken: String): Call<ArrayList<ReportItem>>

    @GET("/report")
    fun getAllReport(@Header("Authorization") accessToken: String): Call<ArrayList<ReportItem>>

    @POST("/report/{markerId}")
    fun report(@Header("Authorization") accessToken: String,
               @Path("markerId") markerId: Long,
               @Query("message") message: String) : Call<Void>

    @PUT("/report/submit/{reportId}")
    fun submitReport(@Header("Authorization") accessToken: String,
                     @Path("reportId") reportId: Long) : Call<Void>

    @DELETE("/report/{reportId}")
    fun deleteReport(@Header("Authorization") accessToken: String,
                     @Path("reportId") reportId: Long) : Call<Void>
}