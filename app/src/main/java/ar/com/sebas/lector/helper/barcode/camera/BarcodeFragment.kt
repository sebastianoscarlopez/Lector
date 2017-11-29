package ar.com.sebas.lector.helper.barcode.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.barcode.BarcodeDetector

import java.io.IOException

import ar.com.sebas.lector.R
import ar.com.sebas.lector.helper.ui.camera.CameraSource
import ar.com.sebas.lector.helper.ui.camera.CameraSourcePreview
import ar.com.sebas.lector.helper.ui.camera.GraphicOverlay
import com.google.android.gms.vision.barcode.Barcode

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
class BarcodeFragment : Fragment() {

    private var mCameraSource: CameraSource? = null
    private var mPreview: CameraSourcePreview? = null
    private var mGraphicOverlay: GraphicOverlay<BarcodeGraphic>? = null

    private val autoFocus = true
    private val useFlash = false
    private var activity: Activity? = null
    private var viewPreview: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewPreview = inflater!!.inflate(R.layout.fragment_barcode, container, false)

        activity = getActivity()
        mPreview = viewPreview!!.findViewById(R.id.preview) as CameraSourcePreview
        mGraphicOverlay = viewPreview!!.findViewById(R.id.graphicOverlay) as GraphicOverlay<BarcodeGraphic>

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        val rc = ActivityCompat.checkSelfPermission(activity!!, android.Manifest.permission.CAMERA)
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash)
        } else {
            requestCameraPermission()
        }
        return viewPreview
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private fun requestCameraPermission() {
        Log.w(TAG, "CAMERA permission is not granted. Requesting permission")

        val permissions = arrayOf(Manifest.permission.CAMERA)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity!!, permissions, RC_HANDLE_CAMERA_PERM)
            return
        }

        val thisActivity = activity

        val listener = View.OnClickListener {
            ActivityCompat.requestPermissions(thisActivity!!, permissions,
                    RC_HANDLE_CAMERA_PERM)
        }

        viewPreview!!.findViewById(R.id.topLayout).setOnClickListener(listener)
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private fun createCameraSource(autoFocus: Boolean, useFlash: Boolean) {

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        val barcodeDetector = BarcodeDetector.Builder(activity).build()
        val barcodeFactory = BarcodeTrackerFactory(mGraphicOverlay!!, mBarcodeUpdateListener!!)
        barcodeDetector.setProcessor(
                MultiProcessor.Builder<Barcode>(barcodeFactory).build())

        if (!barcodeDetector.isOperational) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.")

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            val lowstorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = activity!!.registerReceiver(null, lowstorageFilter) != null

            if (hasLowStorage) {
                Log.w(TAG, getString(R.string.low_storage_error))
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        var builder: CameraSource.Builder = CameraSource.Builder(activity!!.applicationContext, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1600)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
                .setFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                .setRequestedFps(30f)

        mCameraSource = builder.build()
    }


    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        if (mPreview != null) {
            mPreview!!.stop()
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (mPreview != null) {
            try {
                getActivity().runOnUiThread(Runnable {
                    mPreview!!.release()
                })
            }catch (ex: Exception){

            }
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on [.requestPermissions].
     *
     *
     * **Note:** It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     *
     *
     * @param requestCode  The request code passed in [.requestPermissions].
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either [PackageManager.PERMISSION_GRANTED]
     * or [PackageManager.PERMISSION_DENIED]. Never null.
     * @see .requestPermissions
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "CAMERA permission granted - initialize the camera source")
            // we have permission, so create the camerasource
            createCameraSource(autoFocus, useFlash)
            return
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.size +
                " Result code = " + if (grantResults.size > 0) grantResults[0] else "(empty)")

        val listener = DialogInterface.OnClickListener { dialog, id -> activity!!.finish() }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show()
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    @Throws(SecurityException::class)
    private fun startCameraSource() {
        // check that the device has play services available.
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                activity!!.applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(activity, code, RC_HANDLE_GMS)
            dlg.show()
        }

        if (mCameraSource != null) {
            try {
                mPreview!!.start(mCameraSource!!, mGraphicOverlay!!)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                mCameraSource!!.release()
                mCameraSource = null
            }

        }
    }

    companion object {

        private var mBarcodeUpdateListener: BarcodeUpdateListener? = null
        private val TAG = "Barcode-reader"

        // intent request code to handle updating play services if needed.
        private val RC_HANDLE_GMS = 9001

        // permission request codes need to be < 256
        private val RC_HANDLE_CAMERA_PERM = 2

        fun setListener(listener: BarcodeUpdateListener) {
            mBarcodeUpdateListener = listener
        }
    }
}// Required empty public constructor
