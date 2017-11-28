package ar.com.sebas.lector.helper.barcode.camera

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import ar.com.sebas.lector.R

class BarcodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        /*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        //getActionBar().hide();
        setContentView(R.layout.activity_barcode)
    }

    companion object {
        private var instance: BarcodeActivity? = null

        fun stop() {
            if (instance != null && !instance!!.isFinishing) {
                instance!!.finish()
            }
        }
    }
}
