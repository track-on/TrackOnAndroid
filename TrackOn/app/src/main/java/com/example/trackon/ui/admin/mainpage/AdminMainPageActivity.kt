package com.example.trackon.ui.admin.mainpage

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.trackon.R
import com.example.trackon.adapter.ReportListAdapter
import com.example.trackon.method.MakeToast
import com.example.trackon.model.callback.ItemClickListener
import com.example.trackon.model.callback.ItemLongClickListener
import com.example.trackon.model.data.Marker
import com.example.trackon.model.data.ReportItem
import com.example.trackon.model.data.Token
import com.example.trackon.ui.CustomPopup
import com.example.trackon.ui.admin.user_management.UserManagementActivity
import com.example.trackon.ui.login.LoginActivity
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class AdminMainPageActivity : AppCompatActivity(), AdminMainPageContract.View {

    private val makeToast = MakeToast()

    private lateinit var reportListAdapter: ReportListAdapter
    private lateinit var presenter: AdminMainPagePresenter
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var logoutBtn: Button
    private lateinit var locationBtn: Button
    private lateinit var userBtn: Button
    private lateinit var reportList: RecyclerView
    private lateinit var adminRefresh: SwipeRefreshLayout
    private lateinit var view: RelativeLayout
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main_page)

        sharedPreferences = getSharedPreferences("admin", MODE_PRIVATE)
        presenter = AdminMainPagePresenter(this)
        reportListAdapter = ReportListAdapter()

        reportList = findViewById(R.id.admin_report_list)
        logoutBtn = findViewById(R.id.admin_logout_btn)
        locationBtn = findViewById(R.id.admin_location_btn)
        adminRefresh = findViewById(R.id.admin_refresh)
        userBtn = findViewById(R.id.admin_user_btn)
        view = findViewById(R.id.admin_kakao_map)
        map = MapView(this)

        view.addView(map)

        reportList.adapter = reportListAdapter
        reportListAdapter.setOnClickListener(object : ItemClickListener {
            override fun onItemClick(
                viewHolder: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                view: View,
                position: Int
            ) {
                val dialog = CustomPopup(this@AdminMainPageActivity)
                dialog.setTitleAndContent("신고 처리", "신고처리가 완료되었습니까?")
                dialog.setPositiveListener {
                    getAccessToken()?.let { it1 -> presenter.submitReport(it1, reportListAdapter.getReport(position).reportId) }
                    dialog.dismiss()
                }
                dialog.setNegativeListener {
                    makeToast.setToast("취소", this@AdminMainPageActivity)
                    dialog.dismiss()
                }
                dialog.show()
            }

        })

        reportListAdapter.setOnLongClickListener(object : ItemLongClickListener {
            override fun onItemLongClick(
                viewHolder: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                view: View,
                position: Int
            ) {
                val dialog = CustomPopup(this@AdminMainPageActivity)
                dialog.setTitleAndContent("신고 삭제", "신고를 삭제하시겠습니까?")
                dialog.setPositiveListener {
                    getAccessToken()?.let { it1 -> presenter.deleteReport(it1, reportListAdapter.getReport(position).reportId) }
                    dialog.dismiss()
                }
                dialog.setNegativeListener {
                    makeToast.setToast("취소", this@AdminMainPageActivity)
                    dialog.dismiss()
                }
                dialog.show()
            }

        })

        adminRefresh.setOnRefreshListener {
            getAccessToken()?.let { presenter.getAllReports(it) }
            adminRefresh.isRefreshing = false
        }

        getAccessToken()?.let { presenter.getAllReports(it) }

        logoutBtn.setOnClickListener{
            val editor = sharedPreferences.edit()
            editor.remove("accessToken")
            editor.remove("refreshToken")
            editor.remove("isLogin")
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        locationBtn.setOnClickListener {
            makeToast.setToast("추후 업데이트", this)
        }

        userBtn.setOnClickListener {
            val intent = Intent(this, UserManagementActivity::class.java)
            startActivity(intent)
        }
    }

    override fun loadReports(reportItem: ArrayList<ReportItem>) {
        reportListAdapter.setList(reportItem)
        for (item in reportItem) {
            val markerData = Marker(item.markerId, item.name, item.longitude, item.latitude);

            val mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude)

            val marker = MapPOIItem()
            marker.itemName = "ID : ${item.markerId} | NAME : ${item.name}"
            marker.tag = 0
            marker.mapPoint = mapPoint
            marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
            marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin;
            marker.userObject = markerData

            map.addPOIItem(marker)
        }
    }

    override fun retryLoadReports() {
        getAccessToken()?.let { presenter.retryGetAllReports(it) }
    }

    override fun tokenError() {
        getRefreshToken()?.let { presenter.refreshToken(it) }
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

    override fun notAdmin() {
        makeToast.setToast("어드민 권한이 없습니다", this)
        val editor = sharedPreferences.edit()
        editor.remove("accessToken")
        editor.remove("refreshToken")
        editor.remove("isLogin")
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun setNewToken(token: Token) {
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", token.accessToken)
        editor.putString("refreshToken", token.refreshToken)
        editor.apply()
    }

    override fun successSubmit() {
        makeToast.setToast("신고가 정삭적으로 처리되었습니다.", this)
    }

    override fun retrySubmit(reportId: Long) {
        getAccessToken()?.let { presenter.retrySubmitReport(it, reportId) }
    }

    override fun successDelete() {
        makeToast.setToast("신고가 삭제되었습니다.", this)
    }

    override fun retryDelete(reportId: Long) {
        getAccessToken()?.let { presenter.retryDeleteReport(it, reportId) }
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("accessToken", "")
    }

    fun getRefreshToken() : String? {
        return sharedPreferences.getString("refreshToken", "")
    }
}