package com.example.geofence_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Stub for GeofenceBroadcastReceiver.
 * To be implemented by Member 4 (Receiver & Alerts logic).
 */
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("GeofenceReceiver", "Intent received! Member 4 logic will handle this.")
        // Member 4 will extract GeofencingEvent.fromIntent(intent) here.
    }
}
