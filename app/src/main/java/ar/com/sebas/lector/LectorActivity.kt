package ar.com.sebas.lector

import android.app.Activity
import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import ar.com.sebas.lector.helper.barcode.Scanner
import ar.com.sebas.lector.helper.barcode.ScannerEMDK
import ar.com.sebas.lector.helper.barcode.ScannerEnum

class LectorActivity : AppCompatActivity(), Scanner.ScannerListener {
    private var scanner: Scanner? = null
    private val txtData  by bind<TextView>(R.id.txtData)
    val swScanner  by bind<Switch>(R.id.swScanner)

    fun <T : View> Activity.bind(@IdRes res : Int) : Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy(LazyThreadSafetyMode.NONE){ findViewById(res) as T }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lector)
        swScanner.setOnCheckedChangeListener{sw, b ->
            if(sw.isChecked xor (scanner !is ScannerEMDK))
            {
                scanner!!.close()
                val scannerAux = Scanner.create(if(sw.isChecked)
                    ScannerEnum.CAMERA else ScannerEnum.EMDK
                        , this)
                scanner = scannerAux
                scanner?.setScannerListener(this)
            }
            sw.text = if(sw.isChecked) "CAMARA" else "EMDK"
        }
    }

    override fun onResume() {
        super.onResume()
        scanner = Scanner.create(ScannerEnum.DEFAULT, this)
        scanner!!.setScannerListener(this)
        swScanner.isChecked = scanner !is ScannerEMDK
    }

    /**
     * Iniciar lectura
     * @param view sin uso
     */
    fun read(view: View) {
        if(scanner!!.isReady){
            scanner?.read()
        } else {
            Toast.makeText(this, "El lector no esta listo", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Cada lectura de la camara pasa por aquí
     * Se puede realizar una validación antes de cerrar la camara
     * @param data Texto capturado por el lector
     */
    override fun onRead(data: String) {
        txtData!!.text = data
        scanner!!.stop()
    }
}
