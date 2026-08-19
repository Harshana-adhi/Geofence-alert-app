package com.example.geofence_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

        btnStartMonitoring.setOnClickListener {
            startMonitoring()
        }

        requestLocationPermissions()
    }

    private fun startMonitoring() {
        // TODO: Teammate will replace the stub with a real geofencingClient.addGeofences() call
        if (!hasLocationPermissions()) {
            Toast.makeText(
                this,
                "Location permission not granted yet — requesting now.",
                Toast.LENGTH_SHORT
            ).show()
            requestLocationPermissions()
            return
        }

        appendLog("Start Monitoring tapped — registering geofence (stub)")
        tvStatus.text = getString(R.string.status_monitoring_active)
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
        // Android requires background location to be requested separately,
        // after foreground location is already granted, on API 30+.
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
}