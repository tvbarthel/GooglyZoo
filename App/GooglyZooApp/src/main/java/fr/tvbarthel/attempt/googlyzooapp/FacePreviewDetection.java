package fr.tvbarthel.attempt.googlyzooapp;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by tbarthel on 06/02/14.
 */
public class FacePreviewDetection extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = FacePreviewDetection.class.getName();
    private static final double ASPECT_TOLERANCE = 0.2;

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
            Camera.Size optimalSize = getOptimalSize(params.getSupportedPreviewSizes(), w, h);
            params.setPreviewSize(optimalSize.width, optimalSize.height);
            mCamera.setParameters(params);
            mCamera.startPreview();
            startFaceDetection();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * Calculate the optimal size of camera preview
     *
     * @param sizes
     * @param surfaceWidth
     * @param surfaceHeight
     * @return
     */
    private Camera.Size getOptimalSize(List<Camera.Size> sizes, int surfaceWidth, int surfaceHeight) {
        double targetRatio = (double) surfaceWidth / surfaceHeight;
        Camera.Size optimalSize = null;
        double optimalArea = 0;

        // Try to find the size that matches the target ratio and has the max area.
        for (Camera.Size candidateSize : sizes) {
            double candidateRatio = (double) candidateSize.width / candidateSize.height;
            double candidateArea = candidateSize.width * candidateSize.height;
            double ratioDifference = Math.abs(candidateRatio - targetRatio);
            if (ratioDifference < ASPECT_TOLERANCE && candidateArea > optimalArea) {
                optimalSize = candidateSize;
                optimalArea = candidateArea;
            }
        }

        // Cannot find a size that matches the target ratio.
        // Try to find the size that has the max area.
        if (optimalSize == null) {
            optimalArea = 0;
            for (Camera.Size candidateSize : sizes) {
                double candidateArea = candidateSize.width * candidateSize.height;
                if (candidateArea > optimalArea) {
                    optimalSize = candidateSize;
                    optimalArea = candidateArea;
                }
            }
        }

        return optimalSize;
    }

    private void startFaceDetection() {
        Camera.Parameters params = mCamera.getParameters();
        boolean supported = false;
        if (params.getMaxNumDetectedFaces() > 0) {
            mCamera.startFaceDetection();
            supported = true;
        }
        mCallback.onFaceDetectionStart(supported);
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