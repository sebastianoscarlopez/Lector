package ar.com.sebas.lector.helper.barcode;

import android.content.Context;
import java.util.EventListener;

/**
 * Created by lopez.sebastian on 18/10/2017.
 *
 * Lector abstracto para poder implmentar el lector del TC70 o la camara, con una interfaz común
 */

public abstract class AbstractScanner {

    protected Context context;
    ScannerListener scannerListener;

    AbstractScanner(Context context) {
        this.context = context;
    }

    abstract public void Read();
    abstract public void Stop();

    public interface ScannerListener extends EventListener {
        void onRead(String data);
    }

    public void setScannerListener(ScannerListener scannerListener) {
        this.scannerListener = scannerListener;
    }
}
