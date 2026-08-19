package com.example.geofence_app

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

/**
 * GeofenceHelper: Handles the creation and registration of Geofences.
 * 
 * Part of Member 3's task (Geofence + GeofencingClient logic).
 */
object GeofenceHelper {
    private const val TAG = "GeofenceHelper"

    // --- INTEGRATION POINTS FOR TEAMMATES ---
    // Member 1 & 4 must use this exact action string in the Manifest and Receiver
    const val ACTION_GEOFENCE_EVENT = "com.example.geofence_app.ACTION_GEOFENCE_EVENT"
    // Request code used for the PendingIntent
    const val GEOFENCE_REQUEST_CODE = 0 
    // ----------------------------------------

    private const val GEOFENCE_ID = "test_geofence_1"
    private const val LATITUDE = 37.4219999
    private const val LONGITUDE = -122.0862462
    private const val RADIUS_IN_METERS = 100f
    private const val EXPIRATION_DURATION = Geofence.NEVER_EXPIRE

    /**
     * Builds a single fixed Geofence object using hardcoded coordinates.
     */
    private fun getGeofence(): Geofence {
        return Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(LATITUDE, LONGITUDE, RADIUS_IN_METERS)
            .setExpirationDuration(EXPIRATION_DURATION)
            // Trigger when entering or exiting the 100m radius
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    /**
     * Builds a GeofencingRequest which defines how the geofence is triggered.
     */
    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder()
            // INITIAL_TRIGGER_ENTER tells the system to trigger immediately if the user is already inside
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(getGeofence())
            .build()
    }

    /**
     * Creates a PendingIntent that targets GeofenceBroadcastReceiver.
     * The system will send this intent to the receiver when a transition occurs.
     */
    private fun getGeofencePendingIntent(context: Context): PendingIntent {
        // Note: GeofenceBroadcastReceiver will be implemented by Member 4.
        // We use its class here so the PendingIntent knows where to go.
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        
        // FLAG_MUTABLE is required for Geofencing as the system needs to add event data to the intent
        return PendingIntent.getBroadcast(
            context,
            GEOFENCE_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    /**
     * Entry point to register the geofence. 
     * Called by Member 2's button click (after Member 1's permission check).
     */
    fun registerGeofence(context: Context) {
        // Check for required permission before calling the API
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission ACCESS_FINE_LOCATION not granted. Cannot add geofence.")
            return
        }

        val geofencingClient = LocationServices.getGeofencingClient(context)

        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent(context))
            .addOnSuccessListener {
                Log.d(TAG, "Geofence added successfully!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to add geofence: ${e.message}")
            }
    }
}
