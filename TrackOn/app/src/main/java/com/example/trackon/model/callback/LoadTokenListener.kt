package com.example.trackon.model.callback

import com.example.trackon.model.data.Token

interface LoadTokenListener {
    fun loadToken(token: Token)
    fun onFail()
}