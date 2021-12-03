package com.example.trackon.ui.admin.user_management

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.trackon.R
import com.example.trackon.adapter.UserListAdapter
import com.example.trackon.method.MakeToast
import com.example.trackon.model.callback.ItemClickListener
import com.example.trackon.model.data.Token
import com.example.trackon.model.data.User
import com.example.trackon.ui.CustomPopup
import com.example.trackon.ui.login.LoginActivity

class UserManagementActivity : AppCompatActivity(), UserManagementContract.View {

    private val makeToast = MakeToast()

    private lateinit var userListAdapter: UserListAdapter
    private lateinit var presenter: UserManagementPresenter
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var cancelBtn: Button
    private lateinit var userList: RecyclerView
    private lateinit var userRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        sharedPreferences = getSharedPreferences("admin", MODE_PRIVATE)
        presenter = UserManagementPresenter(this)
        userListAdapter = UserListAdapter()

        cancelBtn = findViewById(R.id.user_cancel)
        userList = findViewById(R.id.user_list)
        userRefresh = findViewById(R.id.user_refresh)

        userListAdapter.setOnClickListener(object : ItemClickListener {
            override fun onItemClick(
                viewHolder: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                view: View,
                position: Int
            ) {
                val popup = CustomPopup(this@UserManagementActivity)
                popup.setTitleAndContent("권한부여", "해당 유저를 어드민으로 만드시겠습니까?")
                popup.setPositiveListener {
                    getAccessToken()?.let { it1 -> presenter.makeAdmin(it1, userListAdapter.getUser(position).userId) }
                    popup.dismiss()
                }
                popup.setNegativeListener {
                    makeToast.setToast("취소", this@UserManagementActivity)
                    popup.dismiss()
                }
                popup.show()
            }

        })

        userList.adapter = userListAdapter

        getAccessToken()?.let { presenter.getUsers(it) }

        userRefresh.setOnRefreshListener {
            getAccessToken()?.let { presenter.getUsers(it) }
            userRefresh.isRefreshing = false
        }

        cancelBtn.setOnClickListener {
            super.onBackPressed()
        }
   }

    override fun setNewToken(token: Token) {
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", token.accessToken)
        editor.putString("refreshToken", token.refreshToken)
        editor.apply()
    }

    override fun setUsers(users: ArrayList<User>) {
        userListAdapter.setList(users)
    }

    override fun successMakeAdmin() {
        makeToast.setToast("어드민이 되었습니다!", this)
    }

    override fun retryMakeAdmin(userId: Long) {
        getAccessToken()?.let { presenter.retryMakeAdmin(it, userId) }
    }

    override fun tokenError() {
        getRefreshToken()?.let { presenter.refreshToken(it) }
    }

    override fun notAdmin() {
        makeToast.setToast("어드민이 아닙니다.", this)
        val editor = sharedPreferences.edit()
        editor.remove("accessToken")
        editor.remove("refreshToken")
        editor.remove("isLogin")
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun gotoLogin() {
        val editor = sharedPreferences.edit()
        editor.remove("accessToken")
        editor.remove("refreshToken")
        editor.remove("isLogin")
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun retrySetUser() {
        getAccessToken()?.let { presenter.retryGetUser(it) }
    }

    fun getAccessToken() : String? {
        return sharedPreferences.getString("accessToken", "")
    }

    fun getRefreshToken() : String? {
        return sharedPreferences.getString("refreshToken", "")
    }
}