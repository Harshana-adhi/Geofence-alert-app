package com.example.geofence_app

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

/**
 * GeofenceHelper wraps the Google Play Services GeofencingClient to simplify
 * adding and removing a single fixed geofence.
 *
 * NOTE: Ensure the teammate's BroadcastReceiver is correctly registered in the Manifest
 * with the action matching [GEOFENCE_ACTION].
 */
class GeofenceHelper(private val context: Context) {

    companion object {
        // Geofence constants - hardcoded for this hackathon build
        private const val LATITUDE = 37.4220
        private const val LONGITUDE = -122.0841
        private const val RADIUS_METERS = 100f
        private const val GEOFENCE_ID = "TEAM_FIXED_GEOFENCE_ID"

        // Action string must match the intent-filter in AndroidManifest.xml
        const val GEOFENCE_ACTION = "com.example.geofence_app.ACTION_GEOFENCE_EVENT"
    }

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    // PendingIntent that will be triggered when a geofence transition occurs
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = GEOFENCE_ACTION
        }
        
        // FLAG_MUTABLE is required for geofencing PendingIntents on API 31+
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        PendingIntent.getBroadcast(context, 0, intent, flags)
    }

    /**
     * Starts tracking the fixed geofence.
     * Permission checks (Fine & Background location) are assumed to be handled by caller.
     */
    @SuppressLint("MissingPermission")
    fun startGeofence(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(LATITUDE, LONGITUDE, RADIUS_METERS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(request, geofencePendingIntent)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    /**
     * Stops tracking the fixed geofence by removing it from the GeofencingClient.
     */
    fun stopGeofence() {
        geofencingClient.removeGeofences(geofencePendingIntent)
    }
}
