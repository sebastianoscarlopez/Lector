package ar.com.sebas.lector.helper.barcode

import android.content.Context
import android.util.Log

/**
 * Created by lopez.sebastian on 06/03/2017.
 *
 * Clase vacia para evitar error de compilación
 *
 */

class ScannerEMDK(context: Context) : ar.com.sebas.lector.helper.barcode.Scanner(context) {

    init {
        isReady = false
        Log.d("ScannerEMDK", "Compilando en SDK")
    }

    override fun stop() {
    }

    override fun read() {
    }
}
