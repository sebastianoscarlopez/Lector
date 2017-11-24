package ar.com.sebas.lector.helper.barcode;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;
import java.util.ArrayList;
import java.util.EventListener;

/**
 * Created by lopez.sebastian on 06/03/2017.
 *
 */

@SuppressWarnings("unused")
public class ScannerEMDK extends ar.com.sebas.lector.helper.barcode.Scanner implements EMDKListener, DataListener, StatusListener {

    private boolean scanned;
    private boolean isScanning;
    private EMDKManager emdkManager;
    private Scanner scanner;
    private ProgressDialog pDialog;

    public ScannerEMDK(Context context)
    {
        super(context);
        EMDKResults results = EMDKManager.getEMDKManager(context, this);
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Log.e("ScannerEMDK", "Falló el EMDK Manager");
        }
    }

    public void stop() {
        try {
            if (emdkManager != null) {
                emdkManager.release();
                emdkManager = null;
            }
            if (scanner != null) {
                // releases the scanner hardware resources for other application
                // to use. You must call this as soon as you're done with the
                // scanning.
                scanner.disable();
                scanner = null;
            }
        } catch (ScannerException e) {
            e.printStackTrace();
        }
    }

    public interface ScannerListener extends EventListener {
        void onRead(String data);
    }

    // Method to initialize and enable Scanner and its listeners
    private void initializeScanner() throws ScannerException {
        if (scanner == null) {

            // Get the Barcode Manager object
            BarcodeManager barcodeManager = (BarcodeManager) this.emdkManager
                    .getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

            // Get default scanner defined on the device
            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
            // scanner = barcodeManager.getDevice(list.get(0));

            // Add data and status listeners
            scanner.addDataListener(this);
            scanner.addStatusListener(this);

            // The trigger type is set to HARD by default and HARD is not
            // implemented in this release.
            // So set to SOFT_ALWAYS
            scanner.triggerType = Scanner.TriggerType.SOFT_ALWAYS;

            // Enable the scanner
            scanner.enable();
        }

    }

    @Override
    public void Read() {
        try {
            if (scanner == null) {
                initializeScanner();
            }

            if (scanner != null && !isScanning) {
                // Starts an asynchronous Scan. The method will not turn on
                // the scanner. It will, however, put the scanner in a state
                // in which the scanner can be turned ON either by pressing
                // a hardware trigger or can be turned ON automatically.
                scanned = true;
                scanner.read();
            }

        } catch (Exception e) {
            // Display if there is any exception while performing operation
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void Stop() {

    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        new AsyncDataUpdate().execute(scanDataCollection);
    }

    // AsyncTask that configures the scanned data on background
    // thread and updated the result on UI thread with scanned data and type of
    // label
    private class AsyncDataUpdate extends
            AsyncTask<ScanDataCollection, Void, String> {

        @Override
        protected String doInBackground(ScanDataCollection... params) {
            ScanDataCollection scanDataCollection = params[0];

            // Status string that contains both barcode data and type of barcode
            // that is being scanned
            String statusStr = "";

            // The ScanDataCollection object gives scanning result and the
            // collection of ScanData. So check the data and its status
            if (scanDataCollection != null
                    && scanDataCollection.getResult() == ScannerResults.SUCCESS) {

                ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();

                // Iterate through scanned data and prepare the statusStr
                for (ScanDataCollection.ScanData data : scanData) {
                    // Get the scanned data
                    // Get the type of label being scanned
//                    ScanDataCollection.LabelType labelType = data.getLabelType();
                    // Concatenate barcode data and label type
                    statusStr = data.getData();
                }
            }

            // Return result to populate on UI thread
            return statusStr;
        }

        @Override
        protected void onPostExecute(String result) {
            // Update the dataView EditText on UI thread with barcode data and
            // its label type

            if(scanned) {
                if(scannerListener != null) {
                    scannerListener.onRead(result);
                }
                scanned = false;
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @Override
    public void onStatus(StatusData statusData) {
        new AsyncStatusUpdate().execute(statusData);
    }
    // AsyncTask that configures the current state of scanner on background
    // thread and updates the result on UI thread
    private class AsyncStatusUpdate extends AsyncTask<StatusData, Void, String> {

        @Override
        protected String doInBackground(StatusData... params) {
            // Get the current state of scanner in background
            StatusData statusData = params[0];
            String statusStr = "";
            StatusData.ScannerStates state = statusData.getState();
            // Different states of Scanner
            switch (state) {
                // Scanner is IDLE
                case IDLE:
                    statusStr = "El lector esta inactivo";
                    isScanning = false;
                    break;
                // Scanner is SCANNING
                case SCANNING:
                    statusStr = "Escaneando..";
                    isScanning = true;
                    break;
                // Scanner is waiting for trigger press
                case WAITING:
                    statusStr = "Esperando se presione el disparador..";
                    break;
                default:
                    break;
            }
            // Return result to populate on UI thread
            return statusStr;
        }

        @Override
        protected void onPostExecute(String status) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;
        setScannerParameters();
    }

    @Override
    public void onClosed() {
        if (this.emdkManager != null) {
            this.emdkManager.release();
            this.emdkManager = null;
        }
    }

    // Method to set some decoder parameters in the ScannerConfig object
    private void setScannerParameters() {
        try {

            if (scanner == null) {
                // Method call to initialize the scanner parameters
                initializeScanner();
            }

            ScannerConfig config = scanner.getConfig();
            // Set the code128
            config.decoderParams.code128.enabled = true;
            // set code39
            config.decoderParams.code39.enabled = true;
            // set UPCA
            config.decoderParams.upca.enabled = true;
            scanner.setConfig(config);

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
