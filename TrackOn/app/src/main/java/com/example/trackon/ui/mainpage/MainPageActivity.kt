package com.example.trackon.ui.mainpage

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import app.akexorcist.bluetotohspp.library.DeviceList
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
    private lateinit var bluetooth: Button
    private lateinit var map: MapView
    private lateinit var view:RelativeLayout
    private lateinit var lightBtn: Button

    private lateinit var socket: Socket
    private val bt: BluetoothSPP = BluetoothSPP(this)

    private var buttonName = "?????????"
    private var isLocation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        if(!bt.isBluetoothAvailable) {
            makeToast.setToast("Bluetooth is not available", this)
            gotoLogin()
        }

        tokens = getSharedPreferences("token", MODE_PRIVATE)

        name = findViewById(R.id.user_name)
        logout = findViewById(R.id.logout_btn)
        view = findViewById(R.id.kakao_map)
        locationBtn = findViewById(R.id.location_btn)
        cancelBtn = findViewById(R.id.cancel_btn)
        map = MapView(this)
        bluetooth = findViewById(R.id.bluetooth)
        lightBtn = findViewById(R.id.light_on)

        view.addView(map)

        presenter.getName(getAccessToken())

        locationBtn.text = buttonName

        bt.setOnDataReceivedListener{ data, message ->
            Log.d("blue", message)
            println("????????? ??????")
        }

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
                    makeToast.setToast("GPS??? ????????????", this)
                }

                makeToast.setToast("??????????????? ???????????? ????????? ???????????????!", this)
                isLocation = false
                locationBtn.text = "??????"
            }else {
                stopTracking()
                makeToast.setToast("????????? ??????", this)

                isLocation = true
                locationBtn.text = "?????????"
            }
        }

        cancelBtn.setOnClickListener {
            val dialog = CustomDialog(this)
            dialog.setNegativeListener {
                makeToast.setToast("??????", this)
                dialog.dismiss()
            }

            dialog.setPositiveListener {
                val id = dialog.editId.text.toString()
                val message = dialog.editMessage.text.toString()
                if(id.isEmpty()) {
                    makeToast.setToast("???????????? ??????????????? ???????????????", this)
                    return@setPositiveListener
                }
                presenter.report(id.toLong(), getAccessToken(), message)
                dialog.dismiss()
            }

            dialog.show()
        }

        bluetooth.setOnClickListener {
            if(bt.serviceState == BluetoothState.STATE_CONNECTED) {
                bt.disconnect()
            }else {
                val intent = Intent(applicationContext, DeviceList::class.java)
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE)
            }
        }


        bt.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceConnected(name: String?, address: String?) {
                makeToast.setToast("connected : $name", this@MainPageActivity)
                Log.d(address, "$name connected")
            }

            override fun onDeviceDisconnected() {
                makeToast.setToast("disconnect", this@MainPageActivity)
                Log.d("log", "disconnected")
            }

            override fun onDeviceConnectionFailed() {
                makeToast.setToast("failed connecting", this@MainPageActivity)
                Log.d("failed", "failed")
            }

        })

        lightBtn.setOnClickListener {
            if(bt.serviceState != BluetoothState.STATE_CONNECTED) {
                makeToast.setToast("??????????????? ???????????? ?????? ????????????.", this)
                return@setOnClickListener
            }

            bt.send("1", true)
        }

        lightBtn.setOnLongClickListener {
            bt.disconnect()
            return@setOnLongClickListener true
        }
    }

    override fun onDestroy() {
        bt.stopService()
        super.onDestroy()
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
        makeToast.setToast("?????????????????????.", this)
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
            // ????????? ?????? ??????
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                // ?????? ?????? (?????? ??? ??? ?????????)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("?????? ????????? ?????????????????? ?????? ????????? ??????????????????.")
                builder.setPositiveButton("??????") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION.toInt())
                }
                builder.setNegativeButton("??????") { dialog, which ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // ?????? ?????? ??????
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION.toInt())
                } else {
                    // ?????? ?????? ?????? ?????? (??? ?????? ???????????? ??????)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("?????? ????????? ?????????????????? ???????????? ?????? ????????? ??????????????????.")
                    builder.setPositiveButton("???????????? ??????") { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("??????") { dialog, which ->

                    }
                    builder.show()
                }
            }
        } else {
            // ????????? ?????? ??????
            startTracking()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION.toInt()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ?????? ?????? ??? ????????? (?????? ??????)
                Toast.makeText(this, "?????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show()
                startTracking()
            } else {
                // ?????? ?????? ??? ????????? (?????? ?????? or ?????????)
                Toast.makeText(this, "?????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }

    private var markerIndex = HashMap<Long, MapPOIItem>()

    private val onMarker: Emitter.Listener = Emitter.Listener {
        args ->
        val json = args[0].toString()

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
                val markerData = Marker(id, name, longitude, latitude)

                val mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)

                val marker = MapPOIItem()
                marker.itemName = "ID : $id | NAME : $name"
                marker.tag = 0
                marker.mapPoint = mapPoint
                marker.markerType = MapPOIItem.MarkerType.BluePin // ???????????? ???????????? BluePin ?????? ??????.
                marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                marker.userObject = markerData

                markerIndex[id] = marker

                map.addPOIItem(marker)
            }
        }
    }

    // GPS??? ??????????????? ??????
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // ???????????? ??????
    private fun startTracking() {
        map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    // ???????????? ??????
    private fun stopTracking() {
        map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    override fun onStart() {
        super.onStart()
        if (!bt.isBluetoothEnabled()) { //
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID??? ??????????????? ?????? ??????
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data)
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService()
                bt.startService(BluetoothState.DEVICE_OTHER)
                bt.send("connect", true)
            } else {
                Toast.makeText(
                    applicationContext
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}