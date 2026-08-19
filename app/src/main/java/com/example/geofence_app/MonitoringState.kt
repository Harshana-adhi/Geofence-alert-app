package com.example.geofence_app

import android.content.Context

object MonitoringState {
    private const val PREFS_NAME = "geofence_prefs"
    private const val KEY_IS_MONITORING = "is_monitoring"

    fun setMonitoring(context: Context, isMonitoring: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_MONITORING, isMonitoring)
            .apply()
    }

    fun isMonitoring(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_MONITORING, false)
    }
}
