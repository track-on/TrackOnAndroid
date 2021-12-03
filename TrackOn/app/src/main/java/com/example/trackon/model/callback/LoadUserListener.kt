package com.example.trackon.model.callback

import com.example.trackon.model.data.User

interface LoadUserListener {
    fun loadUser(user: User)
    fun tokenError()
}