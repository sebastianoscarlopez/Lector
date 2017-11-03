package ar.com.sebas.lector.helper.barcode.camera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import ar.com.sebas.lector.R;

public class BarcodeActivity extends AppCompatActivity {
    private static BarcodeActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        instance = this;
    }

    public static void Stop() {
        if(instance != null && !instance.isFinishing()) {
            instance.finish();
        }
    }
}
