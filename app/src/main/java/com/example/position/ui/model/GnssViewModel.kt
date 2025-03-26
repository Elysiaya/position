package com.example.position.ui.model
import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.GnssStatus.CONSTELLATION_BEIDOU
import android.location.GnssStatus.CONSTELLATION_GALILEO
import android.location.GnssStatus.CONSTELLATION_GLONASS
import android.location.GnssStatus.CONSTELLATION_GPS
import android.location.GnssStatus.CONSTELLATION_IRNSS
import android.location.GnssStatus.CONSTELLATION_QZSS
import android.location.GnssStatus.CONSTELLATION_SBAS
import android.location.GnssStatus.CONSTELLATION_UNKNOWN
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class SatellitePosition(
    val svid: Int,
    val azimuth: Float,
    val elevation: Float,
    val snr: Float,
    val usedInFix: Boolean,
    val GNSSType: String,
    val Freq: Float,
    val NavMsg: Boolean
)
class GnssViewModel(context: Context) : ViewModel() {

    //GNSS卫星状态信息
    private val _gnssStatus = MutableStateFlow<GnssStatus?>(null)
    val gnssStatus: MutableStateFlow<GnssStatus?> get() = _gnssStatus
    // 卫星位置数据流
    private val _satellitePositions = MutableStateFlow<List<SatellitePosition>>(emptyList())
    val satellitePositions: StateFlow<List<SatellitePosition>> = _satellitePositions.asStateFlow()
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
            val satellites = mutableListOf<SatellitePosition>()
            for (i in 0 until status.satelliteCount){
                satellites.add(
                    SatellitePosition(
                        svid = status.getSvid(i),
                        azimuth = status.getAzimuthDegrees(i),
                        elevation = status.getElevationDegrees(i),
                        snr = status.getCn0DbHz(i),
                        usedInFix = status.usedInFix(i),
                        GNSSType = getGNSSType(status.getConstellationType(i)),
                        Freq = status.getCarrierFrequencyHz(i),
                        NavMsg = status.hasEphemerisData(i)
                    )
                )
            }
            _satellitePositions.value = satellites
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
fun getGNSSType(satelliteIndex:Int):String
{
    when(satelliteIndex)
    {
//        中国国旗
        CONSTELLATION_BEIDOU -> return "\uD83C\uDDE8\uD83C\uDDF3"
//        美国国旗
        CONSTELLATION_GPS -> return "\uD83C\uDDFA\uD83C\uDDF2"
//        欧盟旗帜
        CONSTELLATION_GALILEO -> return "\uD83C\uDDEA\uD83C\uDDFA"
//        俄罗斯
        CONSTELLATION_GLONASS -> return "\uD83C\uDDF7\uD83C\uDDFA"
//        日本
        CONSTELLATION_QZSS -> return "\uD83C\uDDEF\uD83C\uDDF5"
//        印度
        CONSTELLATION_IRNSS -> return "\uD83C\uDDEE\uD83C\uDDF3"

        CONSTELLATION_SBAS -> return "SBAS"
        CONSTELLATION_UNKNOWN -> return "unknown"
        else -> {
            return "unknown"
        }
    }
}