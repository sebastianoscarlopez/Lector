package ar.com.sebas.lector;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import ar.com.sebas.lector.helper.barcode.Scanner;
import ar.com.sebas.lector.helper.barcode.enumScanner;

public class LectorActivity extends AppCompatActivity implements Scanner.ScannerListener {
    private Scanner scanner;
    private TextView txtData;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector);
        txtData = (TextView)findViewById(R.id.txtData);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanner = Scanner.create(enumScanner.EMDK, getApplicationContext());
        scanner.setScannerListener(this);
    }

    /**
     * Iniciar lectura
     * @param view sin uso
     */
    public void read(View view) {
        scanner.Read();
    }

    /**
     * Cada lectura de la camara pasa por aquí
     * Se puede realizar una validación antes de cerrar la camara
     * @param data Texto capturado por el lector
     */
    @Override
    public void onRead(String data) {
        scanner.Stop();
        txtData.setText(data);
    }}
