package com.mystrica.util

import android.content.Context
import android.location.LocationManager
import androidx.core.content.ContextCompat.getSystemService


fun canGetLocation(context: Context): Boolean {
    val result = true
    val lm: LocationManager?
    var gpsEnabled = false
    var networkEnabled = false
    lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    // exceptions will be thrown if provider is not permitted.
    try {
        gpsEnabled = lm!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (ex: Exception) {
    }
    try {
        networkEnabled = lm!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    } catch (ex: Exception) {
    }
    return gpsEnabled && networkEnabled
}

