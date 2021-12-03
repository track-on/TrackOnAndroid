package com.example.trackon.ui.sign_up

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.trackon.R
import com.example.trackon.method.MakeToast
import com.example.trackon.ui.login.LoginActivity

class SignUpActivity : AppCompatActivity(), SignUpContract.View {

    private val presenter: SignUpContract.Presenter = SignUpPresenter(this)
    private val makeToast = MakeToast()

    private lateinit var signupBtn: Button
    private lateinit var editId: EditText
    private lateinit var editPassword: EditText
    private lateinit var editNickName: EditText
    private lateinit var editPhone: EditText
    private lateinit var editAge: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signupBtn = findViewById(R.id.signup_btn)
        editId = findViewById(R.id.edit_signup_id)
        editPassword = findViewById(R.id.edit_signup_password)
        editNickName = findViewById(R.id.edit_signup_nickname)
        editAge = findViewById(R.id.edit_age)
        editPhone = findViewById(R.id.edit_number)

        signupBtn.setOnClickListener {
            val id = editId.text.toString()
            val password = editPassword.text.toString()
            val nickname = editNickName.text.toString()
            val age = editAge.text.toString()
            val phoneNumber = editPhone.text.toString()

            if(id.isEmpty() || password.isEmpty() || nickname.isEmpty() || age.isEmpty() || phoneNumber.isEmpty()) {
                makeToast.setToast("정보를 제대로 입력하세요!", this)
                return@setOnClickListener
            }

            presenter.signUp(id, password, nickname, age.toInt(), phoneNumber)
        }
    }

    override fun successSignUp() {
        makeToast.setToast("회원가입 성공!", this)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun failedSignUp() {
        makeToast.setToast("회원가입 실패", this)
    }

}