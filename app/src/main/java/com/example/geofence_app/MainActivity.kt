package com.example.geofence_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvLog: TextView
    private lateinit var btnStartMonitoring: Button
    private lateinit var statusDot: View

    private val foregroundLocationRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
            val fineGranted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (fineGranted || coarseGranted) {
                requestBackgroundLocationIfNeeded()
            } else {
                Toast.makeText(
                    this,
                    "Location permission is required for geofencing to work.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val backgroundLocationRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(
                    this,
                    "Background location is required for geofence alerts when the app is closed.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val notificationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(
                    this,
                    "Notification permission is required to show geofence alerts.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvStatus = findViewById(R.id.tvStatus)
        tvLog = findViewById(R.id.tvLog)
        btnStartMonitoring = findViewById(R.id.btnStartMonitoring)
        statusDot = findViewById(R.id.statusDot)

        NotificationHelper.createNotificationChannel(this)

        btnStartMonitoring.setOnClickListener {
            startMonitoring()
        }

        if (MonitoringState.isMonitoring(this)) {
            setMonitoringActive()
        }

        requestLocationPermissions()
        requestNotificationPermissionIfNeeded()
    }

    override fun onStart() {
        super.onStart()
        GeofenceEventBus.listener = { message -> appendLog(message) }
    }

    override fun onStop() {
        super.onStop()
        GeofenceEventBus.listener = null
    }

    private fun startMonitoring() {
        if (!hasLocationPermissions()) {
            Toast.makeText(
                this,
                "Location permission not granted yet — requesting now.",
                Toast.LENGTH_SHORT
            ).show()
            requestLocationPermissions()
            return
        }

        GeofenceHelper.registerGeofence(
            context = this,
            onSuccess = {
                MonitoringState.setMonitoring(this, true)
                appendLog("Geofence registered successfully")
                setMonitoringActive()
            },
            onFailure = { e ->
                appendLog("Failed to register geofence: ${e.message}")
                Toast.makeText(this, "Failed to start monitoring: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun setMonitoringActive() {
        tvStatus.text = getString(R.string.status_monitoring_active)
        statusDot.setBackgroundResource(R.drawable.dot_active)
    }

    private fun appendLog(message: String) {
        val currentLog = tvLog.text.toString()
        val newLog = if (currentLog == getString(R.string.log_placeholder)) {
            message
        } else {
            "$currentLog\n$message"
        }
        tvLog.text = newLog
    }

    private fun hasLocationPermissions(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        return fineGranted && backgroundGranted
    }

    private fun requestLocationPermissions() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            requestBackgroundLocationIfNeeded()
        } else {
            foregroundLocationRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun requestBackgroundLocationIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val backgroundGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (!backgroundGranted) {
                backgroundLocationRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
