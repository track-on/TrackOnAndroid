package com.example.trackon.ui.admin.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.trackon.R
import com.example.trackon.method.MakeToast
import com.example.trackon.model.data.Token
import com.example.trackon.ui.admin.mainpage.AdminMainPageActivity
import com.example.trackon.ui.mainpage.MainPageActivity

class AdminLoginActivity : AppCompatActivity(), AdminLoginContract.View {

    private lateinit var sharedPreferences: SharedPreferences
    private val makeToast = MakeToast()
    private lateinit var presenter: AdminLoginPresenter

    lateinit var loginBtn: Button
    lateinit var editId: EditText
    lateinit var editPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        loginBtn = findViewById(R.id.admin_login_btn)
        editId = findViewById(R.id.admin_edit_id)
        editPassword = findViewById(R.id.admin_edit_password)

        presenter = AdminLoginPresenter(this)

        loginBtn.setOnClickListener {
            val id = editId.text.toString()
            val password = editPassword.text.toString()

            if(id.isEmpty() || id.isBlank() || password.isBlank() || password.isEmpty()) {
                makeToast.setToast("id 혹은 password를 입력해주세요!", this)
                return@setOnClickListener
            }

            presenter.login(id, password)
        }
    }

    override fun loginFail() {
        makeToast.setToast("로그인 실패", this)
    }

    @SuppressLint("CommitPrefEdits")
    override fun setToken(token: Token) {
        sharedPreferences = getSharedPreferences("admin", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", token.accessToken)
        editor.putString("refreshToken", token.refreshToken)
        editor.putBoolean("isLogin", true)
        editor.apply()

        Log.d("token", "${token.accessToken} ${token.refreshToken}")

        val intent = Intent(this, AdminMainPageActivity::class.java)
        startActivity(intent)
        finish()
    }
}