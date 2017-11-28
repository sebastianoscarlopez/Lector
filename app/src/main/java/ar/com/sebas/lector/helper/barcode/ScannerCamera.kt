package ar.com.sebas.lector.helper.barcode

import android.content.Context
import android.content.Intent
import ar.com.sebas.lector.helper.barcode.camera.BarcodeActivity
import ar.com.sebas.lector.helper.barcode.camera.BarcodeFragment
import ar.com.sebas.lector.helper.barcode.camera.BarcodeUpdateListener
import com.google.android.gms.vision.barcode.Barcode

/**
 * Created by lopez.sebastian on 18/10/2017.
 *
 * Lector de códigos utilizando Mobile Vision de Google Play Services
 */

class ScannerCamera(context: Context) : Scanner(context) {
    init {
        BarcodeFragment.setListener(object : BarcodeUpdateListener {
            override fun onBarcodeDetected(barcode: Barcode) {
                scannerListener.onRead(barcode.rawValue)
            }
        })
        isReady = true
    }

    override fun read() {
        val intentBarcode = Intent(context, BarcodeActivity::class.java)
        context.startActivity(intentBarcode)
    }

    override fun stop() {
        BarcodeActivity.stop()
    }
}
