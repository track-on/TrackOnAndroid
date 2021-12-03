package com.example.trackon.model.data

data class User(
    val userId: Long,
    val nickName: String,
    val phoneNumber: String,
    val age: Int,
    val authority: Authority
)
