package com.example.trackon.model.callback

import com.example.trackon.model.data.User

interface LoadAllUserListener {
    fun loadUser(users: ArrayList<User>)
    fun notAdmin()
    fun onFail()
}