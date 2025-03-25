package com.example.position.ui.model
import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow

class GnssViewModel(private val context: Context) : ViewModel() {

    //GNSS卫星状态信息
    private val _gnssStatus = MutableStateFlow<GnssStatus?>(null)
    val gnssStatus: MutableStateFlow<GnssStatus?> get() = _gnssStatus
    //位置信息
    private val _location = MutableStateFlow<Location?>(null)
    val location: MutableStateFlow<Location?> get() = _location

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    // LocationManager 用于 GNSS 状态
    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    //GNSS位置更新回调
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                _location.value = location
            }
        }
    }
    //卫星状态回调
    private val gnssStatusCallback = object : GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            _gnssStatus.value = status
        }
    }

    @SuppressLint("MissingPermission")
    fun startListening() {
        // 注册 GNSS 状态监听
        locationManager.registerGnssStatusCallback(gnssStatusCallback, Handler(Looper.getMainLooper()))
        //请求位置更新
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        ).build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null // Looper，传 null 表示使用主线程
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun stopListening() {
        locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
