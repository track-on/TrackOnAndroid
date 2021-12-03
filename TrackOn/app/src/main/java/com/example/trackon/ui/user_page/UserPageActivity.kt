package com.example.trackon.ui.user_page

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.trackon.R
import com.example.trackon.adapter.ReportListAdapter
import com.example.trackon.model.callback.ItemClickListener
import com.example.trackon.model.callback.ItemLongClickListener
import com.example.trackon.model.data.ReportItem
import com.example.trackon.model.data.Token
import com.example.trackon.model.data.User
import com.example.trackon.ui.login.LoginActivity

class UserPageActivity : AppCompatActivity(), UserPageContract.View {

    private lateinit var name: TextView
    private lateinit var age: TextView
    private lateinit var phone: TextView
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var reportList: RecyclerView

    private lateinit var preferences: SharedPreferences

    private val presenter = UserPagePresenter(this)
    private lateinit var reportListAdapter: ReportListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_page)

        name = findViewById(R.id.my_name)
        age = findViewById(R.id.my_age)
        phone = findViewById(R.id.my_phone)
        reportList = findViewById(R.id.my_report_list)
        refresh = findViewById(R.id.my_refresh)

        reportListAdapter = ReportListAdapter()
        reportList.adapter = reportListAdapter

        reportListAdapter.setOnClickListener(object : ItemClickListener {
            override fun onItemClick(
                viewHolder: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                view: View,
                position: Int
            ) {
                TODO("Not yet implemented")
            }

        })

        reportListAdapter.setOnLongClickListener(object : ItemLongClickListener {
            override fun onItemLongClick(
                viewHolder: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                view: View,
                position: Int
            ) {
                TODO("Not yet implemented")
            }

        })

        refresh.setOnRefreshListener {
            getAccessToken()?.let { presenter.getUser(it) }
            refresh.isRefreshing = false
        }

        getAccessToken()?.let { presenter.getMyReports(it) }
        getAccessToken()?.let { presenter.getUser(it) }
    }

    override fun loadReports(reportItem: ArrayList<ReportItem>) {
        reportListAdapter.setList(reportItem)
    }

    override fun retryLoadReports() {
        getAccessToken()?.let { presenter.retryGetMyReports(it) }
    }

    override fun tokenError() {
        getRefreshToken()?.let { presenter.refreshToken(it) }
    }

    override fun gotoLogin() {
        preferences = getSharedPreferences("token", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove("accessToken")
        editor.remove("refreshToken")
        editor.remove("isLogined")
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun setNewToken(token: Token) {
        preferences = getSharedPreferences("token", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("accessToken", token.accessToken)
        editor.putString("refreshToken", token.refreshToken)
        editor.apply()
    }

    override fun setUser(user: User) {
        name.text = user.nickName
        println(user.phoneNumber)
        phone.text = user.phoneNumber
        age.text = user.age.toString()
    }

    override fun retrySetUser() {
        getAccessToken()?.let { presenter.retryGetUser(it) }
    }

    fun getAccessToken(): String? {
        preferences = getSharedPreferences("token", MODE_PRIVATE)
        return preferences.getString("accessToken", "")
    }

    fun getRefreshToken(): String? {
        preferences = getSharedPreferences("token", MODE_PRIVATE)
        return preferences.getString("refreshToken", "")
    }
}