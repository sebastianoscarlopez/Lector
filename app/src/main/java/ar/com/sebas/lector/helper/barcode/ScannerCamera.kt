package ar.com.sebas.lector.helper.barcode

import android.content.Context
import android.content.Intent
import ar.com.sebas.lector.helper.barcode.camera.BarcodeActivity
import ar.com.sebas.lector.helper.barcode.camera.BarcodeFragment

/**
 * Created by lopez.sebastian on 18/10/2017.
 *
 * Lector de códigos utilizando Mobile Vision de Google Play Services
 */

class ScannerCamera(context: Context) : Scanner(context) {
    init {
        BarcodeFragment.setListener { barcode -> scannerListener.onRead(barcode.rawValue) }
    }

    override fun read() {
        val intentBarcode = Intent(context, BarcodeActivity::class.java)
        context.startActivity(intentBarcode)
    }

    override fun stop() {
        BarcodeActivity.Stop()
    }
}
