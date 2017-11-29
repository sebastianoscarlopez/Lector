package ar.com.sebas.lector.helper.barcode

import android.content.Context
import java.util.EventListener

/**
 * Created by lopez.sebastian on 18/10/2017.
 *
 * Lector abstracto para poder implmentar el lector del TC70 o la camara, con una interfaz común
 */

abstract class Scanner protected constructor(protected var context: Context) {
    internal lateinit var scannerListener: ScannerListener
    internal var isReady: Boolean = false

    abstract fun read()
    abstract fun stop()
    abstract fun close()

    interface ScannerListener : EventListener {
        fun onRead(data: String)
    }

    fun setScannerListener(scannerListener: ScannerListener) {
        this.scannerListener = scannerListener
    }

    companion object {

        fun create(type: ScannerEnum, context: Context): Scanner? {
            var scan = when (type) {
                ScannerEnum.EMDK -> ScannerEMDK(context)
                ScannerEnum.CAMERA -> ScannerCamera(context)
                ScannerEnum.DEFAULT -> ScannerEMDK(context)
            }
            if(!scan.isReady && type == ScannerEnum.DEFAULT)
            {
                scan = ScannerCamera(context)
            }
            return scan
        }
    }
}

