package com.example.trackon.ui.mainpage

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackon.R
import com.example.trackon.method.MakeToast
import com.example.trackon.model.data.Marker
import com.example.trackon.model.data.Token
import com.example.trackon.model.data.User
import com.example.trackon.ui.CustomDialog
import com.example.trackon.ui.login.LoginActivity
import com.example.trackon.ui.user_page.UserPageActivity
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set


@Suppress("DEPRECATION")
class MainPageActivity : AppCompatActivity(), MainPageContract.View {

    private val presenter = MainPagePresenter(this)
    private lateinit var tokens: SharedPreferences

    private val makeToast = MakeToast()
    private lateinit var user: User

    private lateinit var name: TextView
    private lateinit var logout: Button
    private lateinit var locationBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var map: MapView
    private lateinit var view:RelativeLayout

    private lateinit var socket: Socket

    private var buttonName = "내위치"
    private var isLocation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        tokens = getSharedPreferences("token", MODE_PRIVATE)

        name = findViewById(R.id.user_name)
        logout = findViewById(R.id.logout_btn)
        view = findViewById(R.id.kakao_map)
        locationBtn = findViewById(R.id.location_btn)
        cancelBtn = findViewById(R.id.cancel_btn)
        map = MapView(this)

        view.addView(map)

        presenter.getName(getAccessToken())

        locationBtn.text = buttonName

        val mapPoint = MapPoint.mapPointWithGeoCoord(37.5283169, 126.9294254)
        val address = MapReverseGeoCoder.findAddressForMapPoint("2766585da6e51ecd7c02786b6e8674bf", mapPoint)

        try {
            val options = IO.Options()
            options.forceNew = true
            options.reconnection = true
            options.transports = arrayOf(WebSocket.NAME)
            options.query = "token=${getAccessToken()}"

            socket = IO.socket("http://220.90.237.33:7798/", options)
            Log.d("socket", "conneted")

            socket.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        socket.on("marker", onMarker)

        logout.setOnClickListener {
            gotoLogin()
        }

        name.setOnClickListener {
            val intent = Intent(this, UserPageActivity::class.java)
            startActivity(intent)
        }

        locationBtn.setOnClickListener {
            if(isLocation) {
                if (checkLocationService()) {
                    permissionCheck()
                } else {
                    makeToast.setToast("GPS를 켜주세요", this)
                }

                makeToast.setToast("위치고정을 푸시려면 취소를 눌러주세요!", this)
                isLocation = false
                locationBtn.text = "취소"
            }else {
                stopTracking()
                makeToast.setToast("트랙킹 취소", this)

                isLocation = true
                locationBtn.text = "내위치"
            }
        }

        cancelBtn.setOnClickListener {
            val dialog = CustomDialog(this)
            dialog.setNegativeListener {
                makeToast.setToast("취소", this)
                dialog.dismiss()
            }

            dialog.setPositiveListener {
                val id = dialog.editId.text.toString()
                val message = dialog.editMessage.text.toString()
                if(id.isEmpty()) {
                    makeToast.setToast("아이디와 신고내용을 입력하세요", this)
                    return@setPositiveListener
                }
                presenter.report(id.toLong(), getAccessToken(), message)
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    override fun setName(user: User) {
        name.text = user.nickName
        this.user = user
    }

    override fun retryName() {
        presenter.retryGetName(getAccessToken())
    }

    override fun tokenError() {
        presenter.refreshToken(getRefreshToken())
    }

    override fun gotoLogin() {
        tokens = getSharedPreferences("token", MODE_PRIVATE)
        val editor = tokens.edit()
        editor.putBoolean("isLogin", false)
        editor.remove("accessToken")
        editor.remove("refreshToken")
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun successReport() {
        makeToast.setToast("신고되었습니다.", this)
    }

    override fun retryReport(token: String, markerId: Long, message: String) {
        presenter.retryReport(getAccessToken(), markerId, message)
    }

    fun getAccessToken(): String {
        tokens = getSharedPreferences("token", MODE_PRIVATE)
        return tokens.getString("accessToken", "")!!
    }

    fun getRefreshToken(): String {
        tokens = getSharedPreferences("token", MODE_PRIVATE)
        return tokens.getString("refreshToken", "")!!
    }

    override fun setNewToken(token: Token) {
        tokens = getSharedPreferences("token", MODE_PRIVATE)
        val editor = tokens.edit()
        editor.putString("accessToken", token.accessToken)
        editor.putString("refreshToken", token.refreshToken)
        editor.apply()
    }

    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION.toInt())
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION.toInt())
                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { dialog, which ->

                    }
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태
            startTracking()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION.toInt()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                Toast.makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
                startTracking()
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                Toast.makeText(this, "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }

    private var markerIndex = HashMap<Long, MapPOIItem>()

    private val onMarker: Emitter.Listener = Emitter.Listener {
        args ->
        val json = args[0].toString()
        println(json)

        val jsonObject = JSONObject(json)

        val jsonArray = jsonObject.getJSONArray("markerResponses")
        for(i in 0 until jsonArray.length()) {
            val objects = jsonArray.getJSONObject(i)
            val id = objects.getLong("markerId")
            val name = objects.getString("name")
            val longitude = objects.getDouble("longitude")
            val latitude = objects.getDouble("latitude")
            if(markerIndex[id] != null) {
                map.removePOIItem(markerIndex[id]!!)
                markerIndex[id]!!.mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
                map.addPOIItem(markerIndex[id]!!)
            }else {
                val markerData = Marker(id, name, longitude, latitude);

                val mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)

                val marker = MapPOIItem()
                marker.itemName = "ID : $id | NAME : $name"
                marker.tag = 0
                marker.mapPoint = mapPoint
                marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
                marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin;
                marker.userObject = markerData

                markerIndex[id] = marker

                map.addPOIItem(marker)
            }
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 위치추적 시작
    private fun startTracking() {
        map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    // 위치추적 중지
    private fun stopTracking() {
        map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }
}