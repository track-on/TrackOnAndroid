package com.example.trackon.model

import android.util.Log
import com.example.trackon.model.callback.LoadAllUserListener
import com.example.trackon.model.callback.LoadUserListener
import com.example.trackon.model.callback.PostMakeAdminListener
import com.example.trackon.model.callback.PostSignUpListner
import com.example.trackon.model.data.SignUp
import com.example.trackon.model.data.User
import com.example.trackon.model.service.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class sUserModel() {

    private val userService: UserService

    init {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://220.90.237.33:7799")
            .build()
        userService = retrofit.create(UserService::class.java)
    }

    fun signUp(id: String, password: String, nickName: String, phoneNumber: String, age: Int, postSignUpListner: PostSignUpListner) {
        val call = userService.signUp(SignUp(id, password, nickName, age, phoneNumber))
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.code() == 403) postSignUpListner.onFail()

                if(response.code() == 200) postSignUpListner.successSignUp()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("SignUp", t.toString())
            }

        })
    }

    fun getUserInfo(accessToken: String, loadUserListener: LoadUserListener) {
        val call = userService.getUserInfo(accessToken)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.code() == 200) response.body()?.let { loadUserListener.loadUser(it) }
                if(response.code() == 403) loadUserListener.tokenError()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun makeAdmin(accessToken: String, userId: Long, postMakeAdminListener: PostMakeAdminListener) {
        val call = userService.makeAdmin(accessToken, userId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.code() == 200) postMakeAdminListener.makeAdmin()
                if(response.code() == 403) postMakeAdminListener.onFail()
                if(response.code() == 409) postMakeAdminListener.notAdmin()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getAllUser(accessToken: String, loadAllUserListener: LoadAllUserListener) {
        val call = userService.getAllUser(accessToken)
        call.enqueue(object : Callback<ArrayList<User>> {
            override fun onResponse(call: Call<ArrayList<User>>, response: Response<ArrayList<User>>) {
                if(response.code() == 200) response.body()?.let { loadAllUserListener.loadUser(it) }
                if(response.code() == 409) loadAllUserListener.notAdmin()
                if(response.code() == 403) loadAllUserListener.onFail()
            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}