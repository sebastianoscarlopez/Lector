package ar.com.sebas.lector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import ar.com.sebas.lector.helper.barcode.AbstractScanner;
import ar.com.sebas.lector.helper.barcode.ScannerCamera;

public class LectorActivity extends AppCompatActivity implements AbstractScanner.ScannerListener {

    private ScannerCamera scannerCamera;
    private TextView txtData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector);
        txtData = (TextView)findViewById(R.id.txtData);
        scannerCamera = new ScannerCamera(this);
        scannerCamera.setScannerListener(this);
    }

    /**
     * Iniciar lectura
     * @param view sin uso
     */
    public void read(View view) {
        scannerCamera.Read();
    }

    /**
     * Cada lectura de la camara pasa por aquí
     * Se puede realizar una validación antes de cerrar la camara
     * @param data Texto capturado por el lector
     */
    @Override
    public void onRead(String data) {
        scannerCamera.Stop();
        txtData.setText(data);
    }
}
