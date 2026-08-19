package com.example.geofence_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "GeofenceReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null) {
            Log.e(TAG, "GeofencingEvent was null")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, "Geofence error: $errorMessage")
            GeofenceEventBus.publish("Geofence error: $errorMessage")
            return
        }

        val transitionType = geofencingEvent.geofenceTransition
        val triggeringIds = geofencingEvent.triggeringGeofences
            ?.joinToString { it.requestId }
            ?: "unknown"

        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                val message = "Entered geofence: $triggeringIds"
                Log.d(TAG, message)
                NotificationHelper.showTransitionNotification(
                    context,
                    "Entered geofence",
                    "You have entered the monitored area."
                )
                GeofenceEventBus.publish(message)
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                val message = "Exited geofence: $triggeringIds"
                Log.d(TAG, message)
                NotificationHelper.showTransitionNotification(
                    context,
                    "Exited geofence",
                    "You have left the monitored area."
                )
                GeofenceEventBus.publish(message)
            }

            else -> {
                Log.w(TAG, "Unhandled geofence transition type: $transitionType")
            }
        }
    }
}
