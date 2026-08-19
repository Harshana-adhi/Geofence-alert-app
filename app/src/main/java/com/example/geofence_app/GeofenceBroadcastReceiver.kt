package com.example.geofence_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_GEOFENCE_EVENT = "com.example.geofence_app.ACTION_GEOFENCE_EVENT"
        private const val TAG = "GeofenceBroadcastReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received geofence broadcast: ${intent.action}")
        // TODO: Member 4 to handle GeofencingEvent extraction and transition logic here.
    }
}
