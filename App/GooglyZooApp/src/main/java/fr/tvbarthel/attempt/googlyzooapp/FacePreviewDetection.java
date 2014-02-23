package fr.tvbarthel.attempt.googlyzooapp;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by tbarthel on 06/02/14.
 */
public class FacePreviewDetection extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = FacePreviewDetection.class.getName();

    private SurfaceHolder mHolder;
    private Camera mCamera;

    /**
     * fire faceDectection state (enable | not supported)
     */
    private FacePreviewDetectionCallback mCallback;

    public FacePreviewDetection(Context context, Camera camera, FacePreviewDetectionCallback callback) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        mCallback = callback;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            //get best size for preview for landscape orientation
            //based on whishes ratio;
            Camera.Parameters params = mCamera.getParameters();
            Camera.Size bestSize = getBestPreviewSize(w, h, params);
            params.setPreviewSize(bestSize.width, bestSize.height);
            mCamera.setParameters(params);
            mCamera.startPreview();
            startFaceDetection();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * give the best size based of wishes ratio
     *
     * @param width      wishes width
     * @param height     wishes height
     * @param parameters camera parameters to get supported preview sizes
     * @return
     */
    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        float wishesRatio = (float) width / (float) height;
        float dif = Float.MAX_VALUE;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            final float newDif = Math.abs(wishesRatio - (float) size.width / (float) size.height);
            if (newDif < dif) {
                result = size;
                dif = newDif;
            }
        }
        return result;

    }

    private void startFaceDetection() {
        Camera.Parameters params = mCamera.getParameters();

        if (params.getMaxNumDetectedFaces() > 0) {
            mCamera.startFaceDetection();
            Log.e(TAG, "Face Detection started");
        } else {
            Log.e(TAG, "Face Detection not supported");
        }
    }

    public interface FacePreviewDetectionCallback {
        /**
         * fired when face detection as started
         *
         * @param supported true if faceDectection is supported, otherwise false
         */
        public void onFaceDetectionStart(boolean supported);
    }
}