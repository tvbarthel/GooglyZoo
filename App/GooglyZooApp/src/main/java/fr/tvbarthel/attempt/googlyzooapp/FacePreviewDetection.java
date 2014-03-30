package fr.tvbarthel.attempt.googlyzooapp;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by tbarthel on 06/02/14.
 */
public class FacePreviewDetection extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = FacePreviewDetection.class.getName();

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
        SurfaceHolder surfaceHolder = getHolder();
        if (surfaceHolder != null) {
            surfaceHolder.addCallback(this);
        }

        mCallback = callback;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            startFaceDetection();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Since we don't need to adapt anything to structural changes of the surface,
        // we do nothing here.
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
         * @param supported true if face detection is supported, otherwise false
         */
        public void onFaceDetectionStart(boolean supported);
    }
}