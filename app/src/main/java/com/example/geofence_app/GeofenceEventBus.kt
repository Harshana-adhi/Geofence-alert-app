package com.example.geofence_app

object GeofenceEventBus {
    var listener: ((message: String) -> Unit)? = null

    fun publish(message: String) {
        listener?.invoke(message)
    }
}
