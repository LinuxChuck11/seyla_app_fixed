package com.example.seyla

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private val channelName = "com.example.seyla/permissions"

    companion object {
        private const val REQ_MIC = 1001
        private const val REQ_OVERLAY = 1002
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channelName)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "checkPermissions" -> {
                        val data = mapOf(
                            "microphone" to hasMicrophonePermission(),
                            "overlay" to hasOverlayPermission()
                        )
                        result.success(data)
                    }

                    "requestMicrophonePermission" -> {
                        val granted = requestMicrophonePermission()
                        result.success(granted)
                    }

                    "requestOverlayPermission" -> {
                        val granted = requestOverlayPermission()
                        result.success(granted)
                    }

                    else -> result.notImplemented()
                }
            }
    }

    private fun hasMicrophonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    private fun requestMicrophonePermission(): Boolean {
        if (hasMicrophonePermission()) return true

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQ_MIC
        )

        return false
    }

    private fun requestOverlayPermission(): Boolean {
        if (hasOverlayPermission()) return true

        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, REQ_OVERLAY)

        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_MIC) {
            val granted = grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_OVERLAY) {
            // Overlay permission result handled
        }
    }
}
