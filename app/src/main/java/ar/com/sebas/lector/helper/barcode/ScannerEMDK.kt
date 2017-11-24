package ar.com.sebas.lector.helper.barcode

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKManager.EMDKListener
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.*
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.Scanner.DataListener
import com.symbol.emdk.barcode.Scanner.StatusListener

/**
 * Created by lopez.sebastian on 06/03/2017.
 *
 * Lectura utilizando el lector de los Equipos Motorola TC55 y TC70
 *
 */

class ScannerEMDK(context: Context) : ar.com.sebas.lector.helper.barcode.Scanner(context), EMDKListener, DataListener, StatusListener {

    private var scanned: Boolean = false
    private var isScanning: Boolean = false
    private var emdkManager: EMDKManager? = null
    private var scanner: Scanner? = null

    init {
        val results = EMDKManager.getEMDKManager(context, this)
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Log.e("ScannerEMDK", "Falló el EMDK Manager")
        }
    }

    // Method to initialize and enable Scanner and its listeners
    @Throws(ScannerException::class)
    private fun initializeScanner() {
        if (scanner == null) {

            // Get the Barcode Manager object
            val barcodeManager = this.emdkManager!!
                    .getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as BarcodeManager

            // Get default scanner defined on the device
            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)
            // scanner = barcodeManager.getDevice(list.get(0));

            // Add data and status listeners
            scanner!!.addDataListener(this)
            scanner!!.addStatusListener(this)

            // The trigger type is set to HARD by default and HARD is not
            // implemented in this release.
            // So set to SOFT_ALWAYS
            scanner!!.triggerType = Scanner.TriggerType.SOFT_ALWAYS

            // Enable the scanner
            scanner!!.enable()
        }

    }

    override fun read() {
        try {
            if (scanner == null) {
                initializeScanner()
            }

            if (scanner != null && !isScanning) {
                // Starts an asynchronous Scan. The method will not turn on
                // the scanner. It will, however, put the scanner in a state
                // in which the scanner can be turned ON either by pressing
                // a hardware trigger or can be turned ON automatically.
                scanned = true
                scanner!!.read()
            }

        } catch (e: Exception) {
            // Display if there is any exception while performing operation
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }

    }

    override fun stop() {
        try {
            if (emdkManager != null) {
                emdkManager!!.release()
                emdkManager = null
            }
            if (scanner != null) {
                // releases the scanner hardware resources for other application
                // to use. You must call this as soon as you're done with the
                // scanning.
                scanner!!.disable()
                scanner = null
            }
        } catch (e: ScannerException) {
            e.printStackTrace()
        }
    }

    override fun onData(scanDataCollection: ScanDataCollection) {
        AsyncDataUpdate(this).execute(scanDataCollection)
    }

    override fun onStatus(statusData: StatusData) {
        AsyncStatusUpdate(this).execute(statusData)
    }

    override fun onOpened(emdkManager: EMDKManager) {
        this.emdkManager = emdkManager
        setScannerParameters()
    }

    override fun onClosed() {
        if (this.emdkManager != null) {
            this.emdkManager!!.release()
            this.emdkManager = null
        }
    }

    // Method to set some decoder parameters in the ScannerConfig object
    private fun setScannerParameters() {
        try {

            if (scanner == null) {
                // Method call to initialize the scanner parameters
                initializeScanner()
            }

            val config = scanner!!.config
            // Set the code128
            config.decoderParams.code128.enabled = true
            // set code39
            config.decoderParams.code39.enabled = true
            // set UPCA
            config.decoderParams.upca.enabled = true
            scanner!!.config = config

        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        // AsyncTask that configures the scanned data on background
        // thread and updated the result on UI thread with scanned data and type of
        // label
        private class AsyncDataUpdate(private val scannerEMDK: ScannerEMDK) : AsyncTask<ScanDataCollection, Void, String>() {

            override fun doInBackground(vararg params: ScanDataCollection): String {
                val scanDataCollection = params[0]

                // Status string that contains both barcode data and type of barcode
                // that is being scanned
                var statusStr = ""

                // The ScanDataCollection object gives scanning result and the
                // collection of ScanData. So check the data and its status
                if (scanDataCollection.result == ScannerResults.SUCCESS) {

                    val scanData = scanDataCollection.scanData

                    // Iterate through scanned data and prepare the statusStr
                    for (data in scanData) {
                        // Get the scanned data
                        // Get the type of label being scanned
                        //                    ScanDataCollection.LabelType labelType = data.getLabelType();
                        // Concatenate barcode data and label type
                        statusStr = data.data
                    }
                }

                // Return result to populate on UI thread
                return statusStr
            }

            override fun onPostExecute(result: String) {
                // Update the dataView EditText on UI thread with barcode data and
                // its label type

                if (scannerEMDK.scanned) {
                    scannerEMDK.scannerListener.onRead(result)
                    scannerEMDK.scanned = false
                }

            }

            override fun onPreExecute() {}

            override fun onProgressUpdate(vararg values: Void) {}
        }

        // AsyncTask that configures the current state of scanner on background
        // thread and updates the result on UI thread
        private class AsyncStatusUpdate(private val scannerEMDK: ScannerEMDK) : AsyncTask<StatusData, Void, String>() {
    
            override fun doInBackground(vararg params: StatusData): String {
                // Get the current state of scanner in background
                val statusData = params[0]
                var statusStr = ""
                val state = statusData.state
                // Different states of Scanner
                when (state) {
                // Scanner is IDLE
                    StatusData.ScannerStates.IDLE -> {
                        statusStr = "El lector esta inactivo"
                        scannerEMDK.isScanning = false
                    }
                // Scanner is SCANNING
                    StatusData.ScannerStates.SCANNING -> {
                        statusStr = "Escaneando.."
                        scannerEMDK.isScanning = true
                    }
                // Scanner is waiting for trigger press
                    StatusData.ScannerStates.WAITING -> statusStr = "Esperando se presione el disparador.."
                    else -> {
                    }
                }
                // Return result to populate on UI thread
                return statusStr
            }
    
            override fun onPostExecute(status: String) {}
    
            override fun onPreExecute() {}
    
            override fun onProgressUpdate(vararg values: Void) {}
        }
    }
}
