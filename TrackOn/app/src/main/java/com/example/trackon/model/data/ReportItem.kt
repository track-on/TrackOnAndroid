package com.example.trackon.model.data

data class ReportItem(
    val reportId: Long,
    val markerId: Long,
    val reporterId: Long,
    val reporter: String,
    val name: String,
    val message: String,
    val reportAt: String,
    val reportType: ReportType,
    val longitude: Double,
    val latitude: Double
)
