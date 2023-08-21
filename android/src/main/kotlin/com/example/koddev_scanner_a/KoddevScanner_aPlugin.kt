package com.example.koddev_scanner_a

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.zynksoftware.documentscanner.ScanActivity
import com.zynksoftware.documentscanner.ui.DocumentScanner

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** KoddevScanner_aPlugin */
class KoddevScanner_aPlugin: FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {

  private var delegate: PluginRegistry.ActivityResultListener? = null
  private var binding: ActivityPluginBinding? = null
  private var pendingResult: MethodChannel.Result? = null
  lateinit var activity: Activity
  private val START_DOCUMENT_ACTIVITY: Int = 0x362738

  lateinit var channel: MethodChannel

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(binding.binaryMessenger, "koddev_document_scanner")
    channel.setMethodCallHandler(this)
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    if (call.method == "initScan") {
      val configuration = DocumentScanner.Configuration()
      configuration.imageQuality = 50
      configuration.imageSize = 1000000 // 1 MB
      configuration.imageType = Bitmap.CompressFormat.JPEG
      DocumentScanner.init(activity.baseContext, configuration)
    } else if (call.method == "startScan") {
      this.pendingResult = result
      startScan()
    } else {
      result.notImplemented()
    }
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    addActivityResultListener(binding)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    addActivityResultListener(binding)
  }

  override fun onDetachedFromActivity() {
    this.delegate?.let { this.binding?.removeActivityResultListener(it) }
  }

  fun startScan() {
    val intent = Intent(activity, ScannerActivity::class.java)
    try {
      ActivityCompat.startActivityForResult(
        this.activity,
        intent,
        START_DOCUMENT_ACTIVITY,
        null
      )
    } catch (e: ActivityNotFoundException) {
      pendingResult?.error("ERROR", "FAILED TO START ACTIVITY", null)
    }
  }

  private fun addActivityResultListener(binding: ActivityPluginBinding) {
    this.binding = binding
    if (this.delegate == null) {
      this.delegate = PluginRegistry.ActivityResultListener { requestCode, resultCode, data ->
        if (requestCode != START_DOCUMENT_ACTIVITY) {
          return@ActivityResultListener false
        }
        when (resultCode) {
          Activity.RESULT_OK -> {
            // check for errors
            val error = data?.extras?.get("error") as String?
            if (error != null) {
              throw Exception("error - $error")
            }

            // get an array with scanned document file paths
            val croppedImageResults: ByteArray =
              data?.getByteArrayExtra(
                "croppedImage"
              ) ?: throw Exception("No cropped images returned")
            val originalImageResults: ByteArray =
              data?.getByteArrayExtra(
                "originalImage"
              ) ?: throw Exception("No cropped images returned")

            val numbers = listOf(croppedImageResults, originalImageResults)

            // trigger the success event handler with an array of cropped images
            this.pendingResult?.success(numbers)
            return@ActivityResultListener true
          }
          Activity.RESULT_CANCELED -> {
            // user closed camera
            this.pendingResult?.success(emptyList<ByteArray>())
            return@ActivityResultListener true
          }
          else -> {
            return@ActivityResultListener false
          }
        }
      }
    } else {
      binding.removeActivityResultListener(this.delegate!!)
    }

    binding.addActivityResultListener(delegate!!)
  }

}
