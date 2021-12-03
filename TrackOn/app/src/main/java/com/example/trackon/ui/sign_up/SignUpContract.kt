package com.example.trackon.ui.sign_up

class SignUpContract {
    interface View {
        fun successSignUp()
        fun failedSignUp()
    }

    interface Presenter {
        fun signUp(id: String, password: String, nickName: String, age:Int, phoneNumber: String)
    }
}