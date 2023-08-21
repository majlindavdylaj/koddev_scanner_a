package com.example.koddev_scanner_a

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import com.zynksoftware.documentscanner.ScanActivity
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
import com.zynksoftware.documentscanner.model.ScannerResults

class ScannerActivity: ScanActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan);
        addFragmentContentLayout()
    }

    override fun onClose() {
        finish()
    }

    override fun onError(error: DocumentScannerErrorModel) {
        val err = error.errorMessage?.error
        val intent = Intent()
        intent.putExtra("error", err)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onSuccess(scannerResults: ScannerResults) {
        val intent = Intent()
        intent.putExtra("croppedImage", scannerResults.croppedImageFile?.readBytes())
        intent.putExtra("originalImage", scannerResults.originalImageFile?.readBytes())
        setResult(RESULT_OK, intent)
        finish()
    }
}