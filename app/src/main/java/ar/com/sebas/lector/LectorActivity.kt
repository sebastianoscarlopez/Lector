package ar.com.sebas.lector

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import ar.com.sebas.lector.helper.barcode.Scanner
import ar.com.sebas.lector.helper.barcode.ScannerEnum

class LectorActivity : AppCompatActivity(), Scanner.ScannerListener {
    private var scanner: Scanner? = null
    private var txtData: TextView? = null
    private var pDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lector)
        txtData = findViewById(R.id.txtData) as TextView?
        pDialog = ProgressDialog(this)
        pDialog!!.setCancelable(false)
    }

    override fun onResume() {
        super.onResume()
        scanner = Scanner.create(ScannerEnum.DEFAULT, this)
        scanner!!.setScannerListener(this)
    }

    /**
     * Iniciar lectura
     * @param view sin uso
     */
    fun read(view: View) {
        scanner!!.read()
    }

    /**
     * Cada lectura de la camara pasa por aquí
     * Se puede realizar una validación antes de cerrar la camara
     * @param data Texto capturado por el lector
     */
    override fun onRead(data: String) {
        scanner!!.stop()
        txtData!!.text = data
    }
}
