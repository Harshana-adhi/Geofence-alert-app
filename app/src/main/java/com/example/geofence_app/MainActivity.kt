package com.example.geofence_app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvLog: TextView
    private lateinit var btnStartMonitoring: Button

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
    }

    private fun startMonitoring() {
        // TODO: Teammate will gate this behind a runtime permission check
        // TODO: Teammate will replace the stub with a real geofencingClient.addGeofences() call
        
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
}