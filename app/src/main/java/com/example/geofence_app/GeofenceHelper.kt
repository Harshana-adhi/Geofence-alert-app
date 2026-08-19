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

/**
 * GeofenceHelper: Handles the creation and registration of Geofences.
 *
 * Part of Member 3's task (Geofence + GeofencingClient logic).
 *
 * NOTE: [ACTION_GEOFENCE_EVENT] must match exactly what Member 1 registers in
 * the Manifest's <intent-filter> and what Member 4's BroadcastReceiver expects.
 */
object GeofenceHelper {

    private const val TAG = "GeofenceHelper"

    // --- INTEGRATION POINTS FOR TEAMMATES ---
    const val ACTION_GEOFENCE_EVENT = "com.example.geofence_app.ACTION_GEOFENCE_EVENT"
    private const val GEOFENCE_REQUEST_CODE = 0
    // ----------------------------------------

    // TODO(team): confirm target coordinates before the demo — the two branches
    // used different values (37.4219999,-122.0862462 vs 37.4220,-122.0841).
    // Kept the geofence-setup branch's value for now; change if it should be elsewhere.
    private const val GEOFENCE_ID = "test_geofence_1"
    private const val LATITUDE = 37.4219999
    private const val LONGITUDE = -122.0862462
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

    /**
     * Entry point to register the geofence.
     * Called by Member 2's button click (after Member 1's permission check).
     *
     * onSuccess/onFailure let the caller update the UI (status text, log) directly
     * instead of the result only showing up in Logcat.
     */
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

    /**
     * Stops tracking the fixed geofence by removing it from the GeofencingClient.
     */
    fun stopGeofence(context: Context) {
        getGeofencingClient(context).removeGeofences(getGeofencePendingIntent(context))
    }
}