package ar.com.sebas.lector.helper.barcode.camera

import android.support.annotation.UiThread

import com.google.android.gms.vision.barcode.Barcode

/**
 * Consume the item instance detected from an Activity or Fragment level by implementing the
 * BarcodeUpdateListener interface method onBarcodeDetected.
 */
interface BarcodeUpdateListener {
    @UiThread
    fun onBarcodeDetected(barcode: Barcode)
}