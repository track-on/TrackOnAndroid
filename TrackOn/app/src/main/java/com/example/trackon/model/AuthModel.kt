package com.example.trackon.model

import android.util.Log
import com.example.trackon.model.callback.LoadTokenListener
import com.example.trackon.model.data.Login
import com.example.trackon.model.data.Token
import com.example.trackon.model.service.AuthService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthModel() {

    private val authService: AuthService

    init {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://220.90.237.33:7799")
            .build()
        authService = retrofit.create(AuthService::class.java)
    }

    fun signIn(id: String, password: String, loadTokenListener: LoadTokenListener) {
        val call = authService.signIn(Login(id, password))
        call.enqueue(object: Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if(!response.isSuccessful) {
                    loadTokenListener.onFail()
                    return
                }

                loadTokenListener.loadToken(response.body()!!)
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                Log.d("Login", t.toString())
            }

        })
    }

    fun adminLogin(id:String, password: String, loadTokenListener: LoadTokenListener) {
        val call = authService.adminLogin(Login(id, password))
        call.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if(response.code() == 200) response.body()?.let { loadTokenListener.loadToken(it) }
                if(response.code() == 403) loadTokenListener.onFail()
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun refreshToken(refreshToken: String, loadTokenListener: LoadTokenListener) {
        val call = authService.refreshToken(refreshToken)
        call.enqueue(object: Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if(!response.isSuccessful) {
                    loadTokenListener.onFail()
                    return
                }

                loadTokenListener.loadToken(response.body()!!)
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {

            }

        })
    }
}
