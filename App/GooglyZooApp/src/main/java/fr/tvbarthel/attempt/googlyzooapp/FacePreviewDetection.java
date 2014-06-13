package fr.tvbarthel.attempt.googlyzooapp;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.TextureView;

/**
 * Texture view used to render camera preview
 */
public class FacePreviewDetection extends TextureView implements TextureView.SurfaceTextureListener {

    private static final String TAG = FacePreviewDetection.class.getName();

    private Camera mCamera;

    /**
     * fire faceDetection state (enable | not supported)
     */
    private FacePreviewDetectionCallback mCallback;

    public FacePreviewDetection(Context context, Camera camera, FacePreviewDetectionCallback callback) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        this.setSurfaceTextureListener(this);

        mCallback = callback;
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

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
            startFaceDetection();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

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