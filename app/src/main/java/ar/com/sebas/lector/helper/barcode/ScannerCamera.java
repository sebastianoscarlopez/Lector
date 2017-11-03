package ar.com.sebas.lector.helper.barcode;

import android.content.Context;
import android.content.Intent;
import com.google.android.gms.vision.barcode.Barcode;
import ar.com.sebas.lector.helper.barcode.camera.BarcodeActivity;
import ar.com.sebas.lector.helper.barcode.camera.BarcodeFragment;
import ar.com.sebas.lector.helper.barcode.camera.BarcodeUpdateListener;

/**
 * Created by lopez.sebastian on 18/10/2017.
 *
 * Lector de códigos utilizando Mobile Vision de Google Play Services
 */

public class ScannerCamera extends AbstractScanner {
    public ScannerCamera(Context context) {
        super(context);
        BarcodeFragment.setListener(new BarcodeUpdateListener() {
            @Override
            public void onBarcodeDetected(Barcode barcode) {
                scannerListener.onRead(barcode.rawValue);
            }
        });
    }

    @Override
    public void Read() {
        Intent intentBarcode = new Intent(context, BarcodeActivity.class);
        context.startActivity(intentBarcode);
    }

    @Override
    public void Stop() {
        BarcodeActivity.Stop();
    }
}
