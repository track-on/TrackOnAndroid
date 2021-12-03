package com.example.trackon.ui.sign_up

import com.example.trackon.model.UserModel
import com.example.trackon.model.callback.PostSignUpListner

class SignUpPresenter(view: SignUpContract.View): SignUpContract.Presenter {

    private val userModel = UserModel()

    private val postSignUpListner = object : PostSignUpListner{
        override fun successSignUp() {
            view.successSignUp()
        }

        override fun onFail() {
            view.failedSignUp()
        }

    }

    override fun signUp(id: String, password: String, nickName: String, age: Int, phoneNumber: String) {
        userModel.signUp(id, password, nickName, phoneNumber, age, postSignUpListner)
    }
}