package com.example.trackon.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.trackon.R
import com.example.trackon.method.MakeToast
import com.example.trackon.model.data.Token
import com.example.trackon.ui.admin.login.AdminLoginActivity
import com.example.trackon.ui.admin.mainpage.AdminMainPageActivity
import com.example.trackon.ui.mainpage.MainPageActivity
import com.example.trackon.ui.sign_up.SignUpActivity

class LoginActivity : AppCompatActivity(), LoginContract.View {

    private lateinit var sharedPreferences: SharedPreferences;
    private val presenter: LoginContract.Presenter = LoginPresenter(this)
    private val makeToast = MakeToast()

    lateinit var loginBtn: Button
    lateinit var editId: EditText
    lateinit var editPassword: EditText
    lateinit var signUpText: TextView
    lateinit var adminText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn = findViewById(R.id.login_btn)
        editId = findViewById(R.id.edit_id)
        editPassword = findViewById(R.id.edit_password)
        signUpText = findViewById(R.id.sign_up_text)
        adminText = findViewById(R.id.name)

        sharedPreferences = getSharedPreferences("admin", MODE_PRIVATE)
        if(sharedPreferences.getBoolean("isLogin", false)) {
            val intent = Intent(this, AdminMainPageActivity::class.java)
            startActivity(intent)
            finish()
        }

        sharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
        if(sharedPreferences.getBoolean("isLogin", false)) {
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginBtn.setOnClickListener {
            val id = editId.text.toString()
            val password = editPassword.text.toString()

            if(id.isEmpty() || id.isBlank() || password.isBlank() || password.isEmpty()) {
                makeToast.setToast("id 혹은 password를 입력해주세요!", this)
                return@setOnClickListener
            }

            presenter.login(id, password)
        }

        signUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        adminText.setOnLongClickListener {
            makeToast.setToast("어드민 페이지로 전환합니다.", this@LoginActivity)
            val intent = Intent(this@LoginActivity, AdminLoginActivity::class.java)
            startActivity(intent)
            finish()

            true
        }
    }

    override fun loginFail() {
        makeToast.setToast("로그인 실패", this)
    }

    @SuppressLint("CommitPrefEdits")
    override fun setToken(token: Token) {
        sharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", token.accessToken)
        editor.putString("refreshToken", token.refreshToken)
        editor.putBoolean("isLogin", true)
        editor.apply()

        Log.d("token", "${token.accessToken} ${token.refreshToken}")

        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
        finish()
    }
}