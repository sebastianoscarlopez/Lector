package ar.com.sebas.lector.helper.barcode.camera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ar.com.sebas.lector.R;

public class BarcodeActivity extends AppCompatActivity {
    private static BarcodeActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        /*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //getActionBar().hide();
        setContentView(R.layout.activity_barcode);
    }

    public static void Stop() {
        if(instance != null && !instance.isFinishing()) {
            instance.finish();
        }
    }
}
