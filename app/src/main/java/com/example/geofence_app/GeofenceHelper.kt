package com.example.geofence_app

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

object GeofenceHelper {

    private const val TAG = "GeofenceHelper"

    const val ACTION_GEOFENCE_EVENT = "com.example.geofence_app.ACTION_GEOFENCE_EVENT"
    private const val GEOFENCE_REQUEST_CODE = 0

    private const val GEOFENCE_ID = "test_geofence_1"
    private const val LATITUDE = 6.972621
    private const val LONGITUDE = 79.915442
    private const val RADIUS_IN_METERS = 100f
    private const val EXPIRATION_DURATION = Geofence.NEVER_EXPIRE

    private fun getGeofencingClient(context: Context): GeofencingClient =
        LocationServices.getGeofencingClient(context)

    private fun getGeofence(): Geofence {
        return Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(LATITUDE, LONGITUDE, RADIUS_IN_METERS)
            .setExpirationDuration(EXPIRATION_DURATION)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(getGeofence())
            .build()
    }

    private fun getGeofencePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = ACTION_GEOFENCE_EVENT
        }

        // FLAG_MUTABLE is required for geofencing regardless of API level, since the
        // system needs to write transition event data into the intent before delivery.
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE

        return PendingIntent.getBroadcast(context, GEOFENCE_REQUEST_CODE, intent, flags)
    }

    @SuppressLint("MissingPermission")
    fun registerGeofence(
        context: Context,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            val e = SecurityException("ACCESS_FINE_LOCATION not granted. Cannot add geofence.")
            Log.e(TAG, e.message ?: "Permission missing")
            onFailure(e)
            return
        }

        getGeofencingClient(context)
            .addGeofences(getGeofencingRequest(), getGeofencePendingIntent(context))
            .addOnSuccessListener {
                Log.d(TAG, "Geofence added successfully!")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to add geofence: ${e.message}")
                onFailure(e)
            }
    }

    fun stopGeofence(context: Context) {
        getGeofencingClient(context).removeGeofences(getGeofencePendingIntent(context))
    }
}