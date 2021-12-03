package com.example.trackon.model.service

import com.example.trackon.model.data.Login
import com.example.trackon.model.data.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthService {
    @POST("/auth")
    fun signIn(@Body login: Login): Call<Token>

    @PUT("/auth")
    fun refreshToken(@Header("X-Refresh-Token") refreshToken: String): Call<Token>

    @POST("/auth/admin")
    fun adminLogin(@Body login: Login): Call<Token>
}