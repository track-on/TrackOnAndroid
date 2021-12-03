package com.example.trackon.model.service

import com.example.trackon.model.data.SignUp
import com.example.trackon.model.data.Token
import com.example.trackon.model.data.User
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @POST("/user")
    fun signUp(@Body signUp: SignUp): Call<Void>

    @GET("/user")
    fun getUserInfo(@Header("Authorization") accessToken: String): Call<User>

    @GET("/user/all")
    fun getAllUser(@Header("Authorization") accessToken: String): Call<ArrayList<User>>

    @PUT("/user/{userId}")
    fun makeAdmin(@Header("Authorization") accessToken: String,
                  @Path("userId") userId: Long): Call<Void>
}